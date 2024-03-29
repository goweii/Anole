package per.goweii.android.anole.widget

import android.view.View
import androidx.core.util.ObjectsCompat
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class WindowInsetsViewHelper(
    private val view: View
) {
    companion object {
        const val STATUS_BARS = 1
        const val NAVIGATION_BARS = 1 shl 1
        const val CAPTION_BAR = 1 shl 2
        const val IME = 1 shl 3
        const val SYSTEM_GESTURES = 1 shl 4
        const val MANDATORY_SYSTEM_GESTURES = 1 shl 5
        const val TAPPABLE_ELEMENT = 1 shl 6
        const val DISPLAY_CUTOUT = 1 shl 7
    }

    private var onApplyWindowInsetsListener: OnApplyWindowInsetsListener? = null
    private var lastInsets: WindowInsetsCompat? = null

    var windowInsets: Int = 0
        set(value) {
            if (field != value) {
                field = value
                ViewCompat.requestApplyInsets(view)
            }
        }

    init {
        setupForInsets()
        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(view: View) {
                if (lastInsets == null) {
                    ViewCompat.requestApplyInsets(view)
                }
            }

            override fun onViewDetachedFromWindow(view: View) {
            }
        })
    }

    private fun setupForInsets() {
        if (onApplyWindowInsetsListener == null) {
            onApplyWindowInsetsListener =
                androidx.core.view.OnApplyWindowInsetsListener { _, insets ->
                    setWindowInsets(insets)
                }
        }
        ViewCompat.setOnApplyWindowInsetsListener(view, onApplyWindowInsetsListener)
    }

    private fun setWindowInsets(windowInsets: WindowInsetsCompat): WindowInsetsCompat {
        if (!ObjectsCompat.equals(lastInsets, windowInsets)) {
            lastInsets = windowInsets
            val insets = windowInsets.getInsets(getSysInsetsType())
            view.setPadding(insets.left, insets.top, insets.right, insets.bottom)
        }
        return windowInsets
    }

    private fun getSysInsetsType(): Int {
        var type = 0
        if (windowInsets and STATUS_BARS != 0) {
            type = type or WindowInsetsCompat.Type.statusBars()
        }
        if (windowInsets and NAVIGATION_BARS != 0) {
            type = type or WindowInsetsCompat.Type.navigationBars()
        }
        if (windowInsets and CAPTION_BAR != 0) {
            type = type or WindowInsetsCompat.Type.captionBar()
        }
        if (windowInsets and IME != 0) {
            type = type or WindowInsetsCompat.Type.ime()
        }
        if (windowInsets and SYSTEM_GESTURES != 0) {
            type = type or WindowInsetsCompat.Type.systemGestures()
        }
        if (windowInsets and MANDATORY_SYSTEM_GESTURES != 0) {
            type = type or WindowInsetsCompat.Type.mandatorySystemGestures()
        }
        if (windowInsets and TAPPABLE_ELEMENT != 0) {
            type = type or WindowInsetsCompat.Type.tappableElement()
        }
        if (windowInsets and DISPLAY_CUTOUT != 0) {
            type = type or WindowInsetsCompat.Type.displayCutout()
        }
        return type
    }
}