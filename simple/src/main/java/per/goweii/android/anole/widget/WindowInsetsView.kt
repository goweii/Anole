package per.goweii.android.anole.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.res.use
import per.goweii.android.anole.R

class WindowInsetsView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val windowInsetsViewHelper = WindowInsetsViewHelper(this)

    init {
        context.obtainStyledAttributes(attrs, R.styleable.WindowInsetsView)
            .use { typedArray ->
                windowInsetsViewHelper.windowInsets =
                    typedArray.getInt(R.styleable.WindowInsetsView_windowInsets, 0)
            }
    }
}