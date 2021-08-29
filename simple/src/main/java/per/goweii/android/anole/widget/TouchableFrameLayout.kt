package per.goweii.android.anole.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout

class TouchableFrameLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val gestureDetector = GestureDetector(context, SimpleOnGestureListenerImpl())

    var touchable: Boolean = true
    var onTouch: (FrameLayout.(e: MotionEvent) -> Unit)? = null

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (!touchable) {
            return gestureDetector.onTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(ev: MotionEvent): Boolean {
        return super.onTouchEvent(ev)
    }

    private inner class SimpleOnGestureListenerImpl : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent): Boolean {
            return true
        }

        override fun onSingleTapUp(e: MotionEvent): Boolean {
            onTouch?.invoke(this@TouchableFrameLayout, e)
            return true
        }
    }
}