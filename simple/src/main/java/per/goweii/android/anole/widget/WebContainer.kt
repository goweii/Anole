package per.goweii.android.anole.widget

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.widget.FrameLayout

class WebContainer @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val gestureDetector = GestureDetector(context, SimpleOnGestureListenerImpl())
    private val minVelocity: Int = ViewConfiguration.get(context).scaledMinimumFlingVelocity
    private val velocityTracker: VelocityTracker = VelocityTracker.obtain()

    private var currDirection = 0

    var onDragUp: (() -> Unit)? = null
    var onDragDown: (() -> Unit)? = null
    var onTouch: (WebContainer.(e: MotionEvent) -> Unit)? = null
    var touchable: Boolean = true

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!touchable) {
            return gestureDetector.onTouchEvent(ev)
        }
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                velocityTracker.clear()
                currDirection = 0
            }
        }
        velocityTracker.addMovement(ev)
        when (ev.action) {
            MotionEvent.ACTION_UP -> {
                velocityTracker.computeCurrentVelocity(1000)
                when {
                    velocityTracker.yVelocity > minVelocity -> {
                        if (currDirection <= 0) {
                            currDirection = 1
                            onDragDown?.invoke()
                        }
                    }
                    velocityTracker.yVelocity < -minVelocity -> {
                        if (currDirection >= 0) {
                            currDirection = -1
                            onDragUp?.invoke()
                        }
                    }
                }
            }
        }
        when (ev.action) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                velocityTracker.clear()
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private inner class SimpleOnGestureListenerImpl : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            onTouch?.invoke(this@WebContainer, e)
            return true
        }
    }
}