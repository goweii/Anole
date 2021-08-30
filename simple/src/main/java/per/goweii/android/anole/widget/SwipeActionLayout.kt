package per.goweii.android.anole.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper

class SwipeActionLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val viewDragHelper: ViewDragHelper
    var draggable: Boolean = false
        set(value) {
            field = value
            if (!value) {
                viewDragHelper.capturedView?.let {
                    viewDragHelper.settleCapturedViewAt(0, 0)
                }
            }
        }

    var onDismiss: (() -> Unit)? = null
    var onCollect: (() -> Unit)? = null

    init {
        viewDragHelper = ViewDragHelper.create(this, 1.0f, ViewDragCallback())
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

    private inner class ViewDragCallback : ViewDragHelper.Callback() {
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return draggable
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
            return top.coerceIn(
                getChildMinTop(child),
                getChildMaxTop(child)
            )
        }

        override fun onViewPositionChanged(
            changedView: View,
            left: Int,
            top: Int,
            dx: Int,
            dy: Int
        ) {
            super.onViewPositionChanged(changedView, left, top, dx, dy)
            if (changedView.top >= getChildOriginalTop(changedView)) {
                changedView.alpha = 1F
            } else {
                val range = getChildOriginalTop(changedView) - getChildMinTop(changedView)
                val change = getChildOriginalTop(changedView) - changedView.top
                changedView.alpha = 1F - (change.toFloat() / range.toFloat())
            }
            if (changedView.top <= getChildMinTop(changedView)) {
                onDismiss?.invoke()
            }
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
                    onCollect?.invoke()
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
            val sy = thisView.pivotY - thisView.scaleY * thisView.pivotY
            top += thisView.top
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