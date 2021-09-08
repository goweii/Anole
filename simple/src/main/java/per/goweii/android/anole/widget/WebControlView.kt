package per.goweii.android.anole.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.motion.widget.MotionLayout
import per.goweii.android.anole.R

class WebControlView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {
    private val motionLayout: MotionLayout
    val ivBack: ImageView
    val ivForward: ImageView
    val ivMenu: ImageView
    val cvCount: CardView
    val ivHome: ImageView
    val ivLogo: ImageView
    val tvTitle: TextView
    val tvCount: TextView

    init {
        inflate(context, R.layout.layout_bottom_nav, this)
        motionLayout = findViewById(R.id.motion_layout)
        ivBack = findViewById(R.id.iv_back)
        ivForward = findViewById(R.id.iv_forward)
        ivMenu = findViewById(R.id.iv_menu)
        ivHome = findViewById(R.id.iv_home)
        ivLogo = findViewById(R.id.iv_logo)
        tvTitle = findViewById(R.id.tv_title)
        cvCount = findViewById(R.id.cv_count)
        tvCount = findViewById(R.id.tv_count)
    }

    fun toInfoMode() {
        motionLayout.transitionToEnd()
    }

    fun toActionMode() {
        motionLayout.transitionToStart()
    }
}