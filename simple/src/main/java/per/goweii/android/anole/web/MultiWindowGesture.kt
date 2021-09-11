package per.goweii.android.anole.web

import android.annotation.SuppressLint
import android.view.*

@SuppressLint("ClickableViewAccessibility")
class MultiWindowGesture(
    private val view: View,
    block: (MultiWindowGesture.() -> Unit)?
) {
    private val gestureListener = GestureListenerImpl()
    private val gestureDetector = GestureDetector(view.context, gestureListener)

    var onTouch: ((e: MotionEvent) -> Unit)? = null
    var onDragStart: (() -> Unit)? = null
    var onDragging: ((dx: Float, dy: Float) -> Unit)? = null
    var onDragEnd: ((vx: Float, vy: Float) -> Unit)? = null
    var onSingleTap: (() -> Unit)? = null

    init {
        view.setOnTouchListener { _, e ->
            val e2 = MotionEvent.obtain(e)
            e2.setLocation(e2.rawX, e2.rawY)
            onTouch?.invoke(e2)
            gestureDetector.onTouchEvent(e2)
            when (e.action) {
                MotionEvent.ACTION_UP -> {
                    gestureListener.onUp(e2)
                }
                MotionEvent.ACTION_CANCEL -> {
                    gestureListener.onCancel(e2)
                }
            }
            true
        }
        block?.invoke(this)
    }

    private inner class GestureListenerImpl : GestureDetector.SimpleOnGestureListener() {
        private var dragStartEvent: MotionEvent? = null
        private val isDragging get() = dragStartEvent != null
        private lateinit var velocityTracker: VelocityTracker
        private var maxFlingVelocity: Int = ViewConfiguration.getMaximumFlingVelocity()

        override fun onDown(e: MotionEvent): Boolean {
            if (!::velocityTracker.isInitialized) {
                velocityTracker = VelocityTracker.obtain()
                maxFlingVelocity = ViewConfiguration.get(view.context).scaledMaximumFlingVelocity
            } else {
                velocityTracker.clear()
            }
            if (isDragging) {
                dragStartEvent = null
            }
            return true
        }

        fun onUp(e: MotionEvent) {
            if (isDragging) {
                dragStartEvent = null
                onScrollEnd()
            }
        }

        fun onCancel(e: MotionEvent) {
            if (isDragging) {
                dragStartEvent = null
                onScrollEnd()
            }
        }

        private fun onScrollEnd() {
            velocityTracker.computeCurrentVelocity(1000, maxFlingVelocity.toFloat())
            val vx = velocityTracker.xVelocity
            val vy = velocityTracker.yVelocity
            velocityTracker.clear()
            onDragEnd?.invoke(vx, vy)
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            onSingleTap?.invoke()
            return true
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            val e3 = MotionEvent.obtain(e2)
            e3.setLocation(e2.rawX, e2.rawY)
            velocityTracker.addMovement(e3)
            if (!isDragging) {
                dragStartEvent = e3
                onDragStart?.invoke()
            }
            val dx = e3.x - dragStartEvent!!.x
            val dy = e3.y - dragStartEvent!!.y
            onDragging?.invoke(dx, dy)
            return true
        }
    }
}