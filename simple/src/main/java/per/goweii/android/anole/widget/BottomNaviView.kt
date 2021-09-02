package per.goweii.android.anole.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout

class BottomNaviView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    init {
        orientation = HORIZONTAL
    }
}