package per.goweii.android.anole.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.res.use
import androidx.core.util.ObjectsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import per.goweii.android.anole.R

class WindowInsetsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
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

    private var onApplyWindowInsetsListener: androidx.core.view.OnApplyWindowInsetsListener? = null
    private var lastInsets: WindowInsetsCompat? = null

    private var insets = 0

    init {
        context.obtainStyledAttributes(attrs, R.styleable.WindowInsetsView).use { typedArray ->
            insets = typedArray.getInt(R.styleable.WindowInsetsView_windowInsets, 0)
        }
        setupForInsets()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (lastInsets == null) {
            ViewCompat.requestApplyInsets(this)
        }
    }

    private fun setupForInsets() {
        if (onApplyWindowInsetsListener == null) {
            onApplyWindowInsetsListener =
                androidx.core.view.OnApplyWindowInsetsListener { _, insets ->
                    setWindowInsets(insets)
                }
        }
        ViewCompat.setOnApplyWindowInsetsListener(this, onApplyWindowInsetsListener)
    }

    private fun setWindowInsets(windowInsets: WindowInsetsCompat): WindowInsetsCompat {
        if (!ObjectsCompat.equals(lastInsets, windowInsets)) {
            lastInsets = windowInsets
            val insets = windowInsets.getInsets(getSysInsetsType())
            setPadding(insets.left, insets.top, insets.right, insets.bottom)
        }
        return windowInsets
    }

    private fun getSysInsetsType(): Int {
        var type = 0
        if (insets and STATUS_BARS != 0) {
            type = type or WindowInsetsCompat.Type.statusBars()
        }
        if (insets and NAVIGATION_BARS != 0) {
            type = type or WindowInsetsCompat.Type.navigationBars()
        }
        if (insets and CAPTION_BAR != 0) {
            type = type or WindowInsetsCompat.Type.captionBar()
        }
        if (insets and IME != 0) {
            type = type or WindowInsetsCompat.Type.ime()
        }
        if (insets and SYSTEM_GESTURES != 0) {
            type = type or WindowInsetsCompat.Type.systemGestures()
        }
        if (insets and MANDATORY_SYSTEM_GESTURES != 0) {
            type = type or WindowInsetsCompat.Type.mandatorySystemGestures()
        }
        if (insets and TAPPABLE_ELEMENT != 0) {
            type = type or WindowInsetsCompat.Type.tappableElement()
        }
        if (insets and DISPLAY_CUTOUT != 0) {
            type = type or WindowInsetsCompat.Type.displayCutout()
        }
        return type
    }
}