package per.goweii.android.anole.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.OverScroller
import androidx.core.view.NestedScrollingParent3
import androidx.core.view.NestedScrollingParentHelper
import androidx.core.view.ViewCompat
import androidx.core.view.ViewCompat.NestedScrollType
import androidx.core.view.ViewCompat.ScrollAxis

class HScrollLayout @JvmOverloads constructor(
    context: Context, attrs:
    AttributeSet? = null
) : FrameLayout(context, attrs), NestedScrollingParent3 {
    private val helper = NestedScrollingParentHelper(this)
    private val scroller = OverScroller(context)

    private val touchSlop: Int
    private val minimumVelocity: Int
    private val maximumVelocity: Int

    init {
        isFocusable = true
        descendantFocusability = FOCUS_AFTER_DESCENDANTS
        setWillNotDraw(false)
        val configuration = ViewConfiguration.get(getContext())
        overScrollMode = View.OVER_SCROLL_ALWAYS
        touchSlop = configuration.scaledTouchSlop
        minimumVelocity = configuration.scaledMinimumFlingVelocity
        maximumVelocity = configuration.scaledMaximumFlingVelocity
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        clipChildren = false
        val p = (parent as? ViewGroup?)
        p?.clipChildren = false
    }

    // NestedScrollingParent3

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int,
        @NestedScrollType type: Int,
        consumed: IntArray
    ) {
        onOverScroll(dxUnconsumed, dyUnconsumed)
    }

    // NestedScrollingParent2

    override fun onStartNestedScroll(
        child: View, target: View,
        @ScrollAxis axes: Int,
        @NestedScrollType type: Int
    ): Boolean {
        return axes and ViewCompat.SCROLL_AXIS_HORIZONTAL != 0
    }

    override fun onNestedScrollAccepted(
        child: View, target: View,
        @ScrollAxis axes: Int,
        @NestedScrollType type: Int
    ) {
        helper.onNestedScrollAccepted(child, target, axes, type)
        onStartOverScroll()
    }

    override fun onStopNestedScroll(
        target: View,
        @NestedScrollType type: Int
    ) {
        helper.onStopNestedScroll(target, type)
        onStopOverScroll()
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int,
        @NestedScrollType type: Int
    ) {
        onOverScroll(dxUnconsumed, dyUnconsumed)
    }

    override fun onNestedPreScroll(
        target: View,
        dx: Int, dy: Int,
        consumed: IntArray,
        @NestedScrollType type: Int
    ) {
        val sx = -scrollX
        if (sx > 0) {
            if (dx > sx) {
                consumed[0] = dx - sx
            } else if (dx > 0) {
                consumed[0] = dx
            }
        } else if (sx < 0) {
            if (dx < sx) {
                consumed[0] = dx - sx
            } else if (dx < 0) {
                consumed[0] = dx
            }
        }
        val sy = -scrollY
        if (sy > 0) {
            if (dy > sy) {
                consumed[1] = dy - sy
            } else if (dy > 0) {
                consumed[1] = dy
            }
        } else if (sy < 0) {
            if (dy < sy) {
                consumed[1] = dy - sy
            } else if (dy < 0) {
                consumed[1] = dy
            }
        }
        scrollBy(consumed[0] / 2, consumed[1] / 2)
    }

    // NestedScrollingParent

    override fun onStartNestedScroll(
        child: View, target: View,
        @ScrollAxis nestedScrollAxes: Int
    ): Boolean {
        return onStartNestedScroll(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScrollAccepted(
        child: View, target: View,
        @ScrollAxis nestedScrollAxes: Int
    ) {
        onNestedScrollAccepted(child, target, nestedScrollAxes, ViewCompat.TYPE_TOUCH)
    }

    override fun onStopNestedScroll(target: View) {
        onStopNestedScroll(target, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedScroll(
        target: View,
        dxConsumed: Int, dyConsumed: Int,
        dxUnconsumed: Int, dyUnconsumed: Int
    ) {
        onOverScroll(dxUnconsumed, dyUnconsumed)
    }

    override fun onNestedPreScroll(
        target: View,
        dx: Int, dy: Int,
        consumed: IntArray
    ) {
        onNestedPreScroll(target, dx, dy, consumed, ViewCompat.TYPE_TOUCH)
    }

    override fun onNestedFling(
        target: View,
        velocityX: Float, velocityY: Float,
        consumed: Boolean
    ): Boolean {
        startFling(velocityX.toInt(), velocityY.toInt())
        return true
    }

    override fun onNestedPreFling(
        target: View,
        velocityX: Float, velocityY: Float
    ): Boolean {
        return false
    }

    @ScrollAxis
    override fun getNestedScrollAxes(): Int {
        return helper.nestedScrollAxes
    }

    private fun startFling(velocityX: Int, velocityY: Int) {
    }

    override fun computeScroll() {
        if (scroller.computeScrollOffset()) {
            val x: Int = scroller.currX
            val y: Int = scroller.currY
            scrollTo(x, y)
            invalidate()
        }
    }

    private fun onStartOverScroll() {
        scroller.abortAnimation()
    }

    private fun onOverScroll(dx: Int, dy: Int) {
        scrollBy(dx / 2, dy / 2)
    }

    private fun onStopOverScroll() {
        if (!scroller.isFinished) {
            return
        }
        scroller.startScroll(scrollX, scrollY, -scrollX, -scrollY)
        invalidate()
    }
}