package per.goweii.android.anole.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import per.goweii.android.anole.R

class BottomNavView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    val ivBack: ImageView
    val ivForward: ImageView
    val ivMenu: ImageView
    val rlWindows: RelativeLayout
    val tvWindowsCount: TextView
    val ivHome: ImageView

    init {
        orientation = HORIZONTAL
        inflate(context, R.layout.layout_bottom_nav, this)
        ivBack = findViewById(R.id.iv_back)
        ivForward = findViewById(R.id.iv_forward)
        ivMenu = findViewById(R.id.iv_menu)
        rlWindows = findViewById(R.id.rl_windows)
        tvWindowsCount = findViewById(R.id.tv_windows_count)
        ivHome = findViewById(R.id.iv_home)
    }
}