package per.goweii.android.anole.widget

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.use
import per.goweii.android.anole.R

class WindowInsetsConstraintView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val windowInsetsViewHelper = WindowInsetsViewHelper(this)

    init {
        context.obtainStyledAttributes(attrs, R.styleable.WindowInsetsConstraintView)
            .use { typedArray ->
                windowInsetsViewHelper.windowInsets =
                    typedArray.getInt(R.styleable.WindowInsetsConstraintView_windowInsets, 0)
            }
    }
}