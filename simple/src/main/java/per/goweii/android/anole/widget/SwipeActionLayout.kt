package per.goweii.android.anole.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan

class SwipeActionLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val viewDragHelper: ViewDragHelper
    var draggable: Boolean = false
        set(value) {
            field = value
            onDraggableChanged()
        }

    private var dismissing = false
    private var collecting = false

    var onDismiss: (() -> Unit)? = null
    var onCollect: (() -> Unit)? = null

    init {
        viewDragHelper = ViewDragHelper.create(this, 1.0f, ViewDragCallback())
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        getChildAt(childCount - 1)?.alpha = 1F
        for (i in 0 until childCount - 1) {
            getChildAt(i).alpha = if (draggable) 1F else 0F
        }
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        if (draggable) {
            return viewDragHelper.shouldInterceptTouchEvent(event)
        }
        return super.onInterceptTouchEvent(event)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (draggable) {
            viewDragHelper.processTouchEvent(event)
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private fun onDraggableChanged() {
        if (!draggable) {
            viewDragHelper.capturedView?.let {
                viewDragHelper.smoothSlideViewTo(it, 0, getChildOriginalTop(it))
                ViewCompat.postInvalidateOnAnimation(this)
            }
        }
        for (i in 0 until childCount - 1) {
            getChildAt(i).animate()
                .setDuration(80)
                .alpha(if (draggable) 1F else 0F)
                .start()
        }
    }

    private fun onSwiping(f: Float) {
        when {
            f >= 0.5F -> {
                if (!collecting) {
                    collecting = true
                    performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    onCollect?.invoke()
                }
            }
            else -> {
                collecting = false
            }
        }
        when {
            f < 0F -> {
                if (!dismissing) {
                    dismissing = true
                }
            }
            else -> {
                if (dismissing) {
                    dismissing = false
                }
            }
        }
        val dragView = getChildAt(childCount - 1)
        for (i in 0 until childCount - 1) {
            getChildAt(i).translationY = dragView.top + dragView.translationY
        }
        if (f <= -1F) {
            onDismiss?.invoke()
        }
    }

    private inner class ViewDragCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return draggable && child === getChildAt(childCount - 1)
        }

        override fun onViewCaptured(capturedChild: View, activePointerId: Int) {
            super.onViewCaptured(capturedChild, activePointerId)
            parent?.requestDisallowInterceptTouchEvent(true)
        }

        override fun getViewHorizontalDragRange(child: View): Int {
            return 0
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return getChildMaxTop(child) - getChildMinTop(child)
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return child.left
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top.coerceIn(getChildMinTop(child), getChildMaxTop(child))
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            val newTop = changedView.top
            val minTop = getChildMinTop(changedView)
            val maxTop = getChildMaxTop(changedView)
            val oriTop = getChildOriginalTop(changedView)
            val curMove = newTop.toFloat() - oriTop.toFloat()
            val minMove = minTop.toFloat() - oriTop.toFloat()
            val maxMove = maxTop.toFloat() - oriTop.toFloat()
            val f = when {
                newTop < oriTop -> -abs(curMove / minMove)
                newTop > oriTop -> atan((curMove / maxMove) * (PI / 2F)) / (PI / 2F)
                else -> 0F
            }.toFloat()
            when {
                f > 0F -> {
                    val realMove = (maxMove * f).toInt() + oriTop
                    changedView.alpha = 1F
                    changedView.translationY = (realMove - newTop).toFloat()
                }
                f < 0F -> {
                    val range = oriTop - minTop
                    val change = oriTop - changedView.top
                    changedView.alpha = 1F - (change.toFloat() / range.toFloat())
                    changedView.translationY = 0F
                }
                else -> {
                    changedView.alpha = 1F
                    changedView.translationY = 0F
                }
            }
            onSwiping(f)
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            super.onViewReleased(releasedChild, xvel, yvel)
            when {
                yvel <= -2000 -> {
                    viewDragHelper.settleCapturedViewAt(
                        releasedChild.left,
                        getChildMinTop(releasedChild)
                    )
                    ViewCompat.postInvalidateOnAnimation(this@SwipeActionLayout)
                }
                releasedChild.top < getChildTriggerTop(releasedChild) -> {
                    viewDragHelper.settleCapturedViewAt(
                        releasedChild.left,
                        getChildMinTop(releasedChild)
                    )
                    ViewCompat.postInvalidateOnAnimation(this@SwipeActionLayout)
                }
                releasedChild.top >= getChildMaxTop(releasedChild) -> {
                    viewDragHelper.settleCapturedViewAt(
                        releasedChild.left,
                        getChildOriginalTop(releasedChild)
                    )
                    ViewCompat.postInvalidateOnAnimation(this@SwipeActionLayout)
                }
                else -> {
                    viewDragHelper.settleCapturedViewAt(
                        releasedChild.left,
                        getChildOriginalTop(releasedChild)
                    )
                    ViewCompat.postInvalidateOnAnimation(this@SwipeActionLayout)
                }
            }
        }
    }

    private fun getChildOriginalTop(child: View): Int {
        val lp = child.layoutParams as LayoutParams
        return lp.topMargin + paddingTop
    }

    private fun getChildMinTop(child: View): Int {
        if (clipChildren) return -child.height
        var top = 0
        var thisView: View? = this
        while ((thisView?.parent as? ViewGroup?)?.clipChildren == false) {
            top += thisView.top
            val sy = thisView.pivotY - thisView.scaleY * thisView.pivotY
            top += sy.toInt()
            thisView = thisView.parent as? ViewGroup?
        }
        return -(top + child.height)
    }

    private fun getChildTriggerTop(child: View): Int {
        return getChildOriginalTop(child) - (child.height * 0.5F).toInt()
    }

    private fun getChildMaxTop(child: View): Int {
        return getChildOriginalTop(child) + (child.height * 0.5F).toInt()
    }
}