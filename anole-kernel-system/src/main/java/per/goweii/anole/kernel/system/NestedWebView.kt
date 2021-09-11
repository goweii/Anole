package per.goweii.anole.kernel.system

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.view.animation.AnimationUtils
import android.webkit.WebView
import android.widget.OverScroller
import androidx.core.view.*
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class NestedWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.webViewStyle
) : WebView(context, attrs, defStyleAttr), NestedScrollingChild2, NestedScrollingParent {
    private val mParentHelper: NestedScrollingParentHelper = NestedScrollingParentHelper(this)
    private val mChildHelper: NestedScrollingChildHelper = NestedScrollingChildHelper(this)
    private val mScroller: OverScroller = OverScroller(context)
    private var mVelocityTracker: VelocityTracker? = null

    private val mVerticalScrollFactor by lazy {
        val outValue = TypedValue()
        context.theme.resolveAttribute(android.R.attr.listPreferredItemHeight, outValue, true)
        outValue.getDimension(context.resources.displayMetrics)
    }

    private val mTouchSlop: Int
    private val mMinimumVelocity: Int
    private val mMaximumVelocity: Int

    private val mScrollOffset = IntArray(2)
    private val mScrollConsumed = IntArray(2)
    private var mActivePointerId = INVALID_POINTER
    private var mLastScrollerY = 0
    private var mLastMotionY = 0
    private var mNestedYOffset = 0
    private var mIsBeingDragged = false
    private var mLastScroll: Long = 0

    val scrollRange: Int get() = computeVerticalScrollRange()

    init {
        overScrollMode = OVER_SCROLL_NEVER
        val configuration = ViewConfiguration.get(context)
        mTouchSlop = configuration.scaledTouchSlop
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity
        mMaximumVelocity = configuration.scaledMaximumFlingVelocity
        isNestedScrollingEnabled = true
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun fling(velocityY: Int) {
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH)
        mScroller.fling(scrollX, scrollY, 0, velocityY, 0, 0, Int.MIN_VALUE, Int.MAX_VALUE, 0, 0)
        mLastScrollerY = scrollY
        ViewCompat.postInvalidateOnAnimation(this)
    }

    @Suppress("unused")
    fun stopScroll() {
        mScroller.forceFinished(true)
    }

    fun smoothScrollTo(x: Int, y: Int) {
        smoothScrollBy(x - scrollX, y - scrollY)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    fun smoothScrollBy(dx: Int, dy: Int) {
        var dy0 = dy
        val duration = AnimationUtils.currentAnimationTimeMillis() - mLastScroll
        if (duration > ANIMATED_SCROLL_GAP) {
            val height = height - paddingBottom - paddingTop
            val bottom = getHeight()
            val maxY = max(0, bottom - height)
            val scrollY = scrollY
            dy0 = max(0, min(scrollY + dy0, maxY)) - scrollY
            mScroller.startScroll(scrollX, scrollY, 0, dy0)
            ViewCompat.postInvalidateOnAnimation(this)
        } else {
            if (!mScroller.isFinished) {
                mScroller.abortAnimation()
            }
            scrollBy(dx, dy0)
        }
        mLastScroll = AnimationUtils.currentAnimationTimeMillis()
    }

    override fun requestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        if (disallowIntercept) {
            recycleVelocityTracker()
        }
        super.requestDisallowInterceptTouchEvent(disallowIntercept)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        if (action == MotionEvent.ACTION_MOVE && mIsBeingDragged) {
            return true
        }
        kotlin.run {
            when (action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_MOVE -> {
                    val activePointerId = mActivePointerId
                    if (activePointerId == INVALID_POINTER) {
                        return@run
                    }
                    val pointerIndex = ev.findPointerIndex(activePointerId)
                    if (pointerIndex == INVALID_POINTER) {
                        return@run
                    }
                    val y = ev.getY(pointerIndex).toInt()
                    val yDiff = abs(y - mLastMotionY)
                    if (yDiff > mTouchSlop && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL == 0) {
                        mIsBeingDragged = true
                        mLastMotionY = y
                        initVelocityTrackerIfNotExists()
                        mVelocityTracker?.addMovement(ev)
                        mNestedYOffset = 0
                        parent?.requestDisallowInterceptTouchEvent(true)
                    }
                }
                MotionEvent.ACTION_DOWN -> {
                    mLastMotionY = ev.y.toInt()
                    mActivePointerId = ev.getPointerId(0)
                    initOrResetVelocityTracker()
                    mVelocityTracker?.addMovement(ev)
                    mScroller.computeScrollOffset()
                    mIsBeingDragged = !mScroller.isFinished
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
                }
                MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                    mIsBeingDragged = false
                    mActivePointerId = INVALID_POINTER
                    recycleVelocityTracker()
                    if (mScroller.springBack(scrollX, scrollY, 0, 0, 0, scrollRange)) {
                        ViewCompat.postInvalidateOnAnimation(this)
                    }
                    stopNestedScroll()
                }
                MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)
            }
        }
        return mIsBeingDragged
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        var returnValue = false
        initVelocityTrackerIfNotExists()
        val vtev = MotionEvent.obtain(ev)
        val actionMasked = ev.actionMasked
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            mNestedYOffset = 0
        }
        vtev.offsetLocation(0f, mNestedYOffset.toFloat())
        kotlin.run {
            when (actionMasked) {
                MotionEvent.ACTION_DOWN -> {
                    returnValue = super.onTouchEvent(ev)
                    if (!mScroller.isFinished.also { mIsBeingDragged = it }) {
                        parent?.requestDisallowInterceptTouchEvent(true)
                    }
                    if (!mScroller.isFinished) {
                        mScroller.abortAnimation()
                    }
                    mLastMotionY = ev.y.toInt()
                    mActivePointerId = ev.getPointerId(0)
                    startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
                }
                MotionEvent.ACTION_MOVE -> {
                    val activePointerIndex = ev.findPointerIndex(mActivePointerId)
                    if (activePointerIndex == INVALID_POINTER) {
                        return@run
                    }
                    val y = ev.getY(activePointerIndex).toInt()
                    var deltaY = mLastMotionY - y
                    if (dispatchNestedPreScroll(
                            0,
                            deltaY,
                            mScrollConsumed,
                            mScrollOffset,
                            ViewCompat.TYPE_TOUCH
                        )
                    ) {
                        deltaY -= mScrollConsumed[1]
                        ev.offsetLocation(0f, (-mScrollOffset[1]).toFloat())
                        vtev.offsetLocation(0f, (-mScrollOffset[1]).toFloat())
                        mNestedYOffset += mScrollOffset[1]
                    }
                    var notMove = mScrollOffset[1] == 0
                    if (!mIsBeingDragged && abs(deltaY) > mTouchSlop) {
                        parent?.requestDisallowInterceptTouchEvent(true)
                        mIsBeingDragged = true
                        if (deltaY > 0) {
                            deltaY -= mTouchSlop
                        } else {
                            deltaY += mTouchSlop
                        }
                    }
                    if (mIsBeingDragged) {
                        parent?.requestDisallowInterceptTouchEvent(true)
                        mLastMotionY = y - mScrollOffset[1]
                        val oldY = scrollY
                        val newScrollY = max(0, oldY + deltaY)
                        val scrolledDeltaY = newScrollY - oldY
                        val unconsumedY = deltaY - scrolledDeltaY
                        if (dispatchNestedScroll(
                                0,
                                scrolledDeltaY,
                                0,
                                unconsumedY,
                                mScrollOffset,
                                ViewCompat.TYPE_TOUCH
                            )
                        ) {
                            mLastMotionY -= mScrollOffset[1]
                            vtev.offsetLocation(0f, mScrollOffset[1].toFloat())
                            mNestedYOffset += mScrollOffset[1]
                        }
                    } else {
                        parent?.requestDisallowInterceptTouchEvent(false)
                    }
                    notMove = notMove and (mScrollOffset[1] == 0)
                    if (notMove) {
                        returnValue = super.onTouchEvent(ev)
                    } else {
                        parent?.requestDisallowInterceptTouchEvent(true)
                    }
                }
                MotionEvent.ACTION_UP -> {
                    if (abs(mNestedYOffset) < mTouchSlop) {
                        returnValue = super.onTouchEvent(ev)
                    } else {
                        ev.action = MotionEvent.ACTION_CANCEL
                        returnValue = super.onTouchEvent(ev)
                    }
                    val velocityTracker = mVelocityTracker
                    velocityTracker!!.computeCurrentVelocity(1000, mMaximumVelocity.toFloat())
                    val initialVelocity = velocityTracker.getYVelocity(mActivePointerId).toInt()
                    if (abs(initialVelocity) > mMinimumVelocity) {
                        flingWithNestedDispatch(-initialVelocity)
                    } else if (mScroller.springBack(scrollX, scrollY, 0, 0, 0, scrollRange)) {
                        ViewCompat.postInvalidateOnAnimation(this)
                    }
                    mActivePointerId = INVALID_POINTER
                    endDrag()
                }
                MotionEvent.ACTION_CANCEL -> {
                    returnValue = true
                    if (mIsBeingDragged && childCount > 0) {
                        if (mScroller.springBack(scrollX, scrollY, 0, 0, 0, scrollRange)) {
                            ViewCompat.postInvalidateOnAnimation(this)
                        }
                    }
                    mActivePointerId = INVALID_POINTER
                    endDrag()
                }
                MotionEvent.ACTION_POINTER_DOWN -> {
                    val index = ev.actionIndex
                    mLastMotionY = ev.getY(index).toInt()
                    mActivePointerId = ev.getPointerId(index)
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    onSecondaryPointerUp(ev)
                    mLastMotionY = ev.getY(ev.findPointerIndex(mActivePointerId)).toInt()
                }
            }
        }
        mVelocityTracker?.addMovement(vtev)
        vtev.recycle()
        return returnValue
    }

    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        if (event.source and InputDeviceCompat.SOURCE_CLASS_POINTER != 0) {
            when (event.action) {
                MotionEvent.ACTION_SCROLL -> {
                    if (!mIsBeingDragged) {
                        val vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL)
                        if (vscroll != 0f) {
                            val delta = (vscroll * mVerticalScrollFactor).toInt()
                            val range = scrollRange
                            val oldScrollY = scrollY
                            var newScrollY = oldScrollY - delta
                            if (newScrollY < 0) {
                                newScrollY = 0
                            } else if (newScrollY > range) {
                                newScrollY = range
                            }
                            if (newScrollY != oldScrollY) {
                                super.scrollTo(scrollX, newScrollY)
                                return true
                            }
                        }
                    }
                }
            }
        }
        return false
    }

    override fun onOverScrolled(scrollX: Int, scrollY: Int, clampedX: Boolean, clampedY: Boolean) {
        super.scrollTo(scrollX, scrollY)
    }

    override fun computeScroll() {
        if (mScroller.computeScrollOffset()) {
            val y = mScroller.currY
            var dy = y - mLastScrollerY

            // Dispatch up to parent
            if (dispatchNestedPreScroll(0, dy, mScrollConsumed, null, ViewCompat.TYPE_NON_TOUCH)) {
                dy -= mScrollConsumed[1]
            }
            if (dy != 0) {
                val range = scrollRange
                val oldScrollY = scrollY
                overScrollByCompat(0, dy, scrollX, oldScrollY, 0, range, 0, 0, false)
                val scrolledDeltaY = scrollY - oldScrollY
                val unconsumedY = dy - scrolledDeltaY
                dispatchNestedScroll(
                    0, scrolledDeltaY, 0, unconsumedY, null,
                    ViewCompat.TYPE_NON_TOUCH
                )
            }

            // Finally update the scroll positions and post an invalidation
            mLastScrollerY = y
            ViewCompat.postInvalidateOnAnimation(this)
        } else {
            // We can't scroll any more, so stop any indirect scrolling
            if (hasNestedScrollingParent(ViewCompat.TYPE_NON_TOUCH)) {
                stopNestedScroll(ViewCompat.TYPE_NON_TOUCH)
            }
            // and reset the scroller y
            mLastScrollerY = 0
        }
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper.isNestedScrollingEnabled
    }

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?
    ): Boolean {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun getNestedScrollAxes(): Int {
        return mParentHelper.nestedScrollAxes
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return mChildHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        mChildHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return mChildHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int
    ) {
        val oldScrollY = scrollY
        scrollBy(0, dyUnconsumed)
        val myConsumed = scrollY - oldScrollY
        val myUnconsumed = dyUnconsumed - myConsumed
        dispatchNestedScroll(0, myConsumed, 0, myUnconsumed, null)
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        dispatchNestedPreScroll(dx, dy, consumed, null)
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        if (!consumed) {
            flingWithNestedDispatch(velocityY.toInt())
            return true
        }
        return false
    }

    override fun onNestedPreFling(target: View, velocityX: Float, velocityY: Float): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    // nested scroll parent
    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0
    }

    override fun onNestedScrollAccepted(child: View, target: View, nestedScrollAxes: Int) {
        mParentHelper.onNestedScrollAccepted(child, target, nestedScrollAxes)
        startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
    }

    override fun onStopNestedScroll(target: View) {
        mParentHelper.onStopNestedScroll(target)
        stopNestedScroll()
    }

    @Suppress("SameParameterValue", "UNUSED_PARAMETER")
    private fun overScrollByCompat(
        deltaX: Int, deltaY: Int,
        scrollX: Int, scrollY: Int,
        scrollRangeX: Int, scrollRangeY: Int,
        maxOverScrollX: Int, maxOverScrollY: Int,
        isTouchEvent: Boolean
    ): Boolean {
        var maxOverScrollX0 = maxOverScrollX
        var maxOverScrollY0 = maxOverScrollY
        val overScrollMode = overScrollMode
        val canScrollHorizontal = computeHorizontalScrollRange() > computeHorizontalScrollExtent()
        val canScrollVertical = computeVerticalScrollRange() > computeVerticalScrollExtent()
        val overScrollHorizontal = (overScrollMode == OVER_SCROLL_ALWAYS
                || overScrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && canScrollHorizontal)
        val overScrollVertical = (overScrollMode == OVER_SCROLL_ALWAYS
                || overScrollMode == OVER_SCROLL_IF_CONTENT_SCROLLS && canScrollVertical)
        var newScrollX = scrollX + deltaX
        if (!overScrollHorizontal) {
            maxOverScrollX0 = 0
        }
        var newScrollY = scrollY + deltaY
        if (!overScrollVertical) {
            maxOverScrollY0 = 0
        }

        // Clamp values if at the limits and record
        val left = -maxOverScrollX0
        val right = maxOverScrollX0 + scrollRangeX
        val top = -maxOverScrollY0
        val bottom = maxOverScrollY0 + scrollRangeY
        var clampedX = false
        if (newScrollX > right) {
            newScrollX = right
            clampedX = true
        } else if (newScrollX < left) {
            newScrollX = left
            clampedX = true
        }
        var clampedY = false
        if (newScrollY > bottom) {
            newScrollY = bottom
            clampedY = true
        } else if (newScrollY < top) {
            newScrollY = top
            clampedY = true
        }
        if (clampedY && !hasNestedScrollingParent(ViewCompat.TYPE_NON_TOUCH)) {
            mScroller.springBack(newScrollX, newScrollY, 0, 0, 0, scrollRange)
        }
        onOverScrolled(newScrollX, newScrollY, clampedX, clampedY)
        return clampedX || clampedY
    }

    private fun endDrag() {
        mIsBeingDragged = false
        recycleVelocityTracker()
        stopNestedScroll()
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mLastMotionY = ev.getY(newPointerIndex).toInt()
            mActivePointerId = ev.getPointerId(newPointerIndex)
            mVelocityTracker?.clear()
        }
    }

    private fun initOrResetVelocityTracker() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        } else {
            mVelocityTracker?.clear()
        }
    }

    private fun initVelocityTrackerIfNotExists() {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        } else {
            mVelocityTracker?.recycle()
        }
    }

    private fun recycleVelocityTracker() {
        if (mVelocityTracker != null) {
            mVelocityTracker?.recycle()
            mVelocityTracker = null
        }
    }

    private fun flingWithNestedDispatch(velocityY: Int) {
        val scrollY = scrollY
        val canFling = ((scrollY > 0 || velocityY > 0) && (scrollY < scrollRange || velocityY < 0))
        if (!dispatchNestedPreFling(0f, velocityY.toFloat())) {
            dispatchNestedFling(0f, velocityY.toFloat(), canFling)
            fling(velocityY)
        }
    }

    companion object {
        private const val INVALID_POINTER = -1
        private const val ANIMATED_SCROLL_GAP = 250
    }
}