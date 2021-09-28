package per.goweii.android.anole.web

import android.view.View
import androidx.annotation.FloatRange
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import per.goweii.android.anole.R
import per.goweii.android.anole.widget.SwipeActionLayout
import per.goweii.android.anole.widget.WebContainer

class WebPageTransformer(
    @FloatRange(from = 0.0, to = 1.0)
    val scale: Float,
    private val elevation: Float,
    private val gap: Float,
    private val cornerRadius: Float
) : ViewPager2.PageTransformer {
    var viewPager: ViewPager2? = null

    var faction: Float = 0F
        set(value) {
            field = value.coerceIn(-1F, 2F)
            viewPager?.requestTransform()
        }

    override fun transformPage(page: View, position: Float) {
        val scale = 1F - (1F - scale) * faction
        page.pivotX = page.width / 2F
        page.pivotY = page.height / 2F
        page.scaleX = scale
        page.scaleY = scale
        page.translationX = -(page.width - (page.width * scale) - gap) * position
        val swipeActionLayout = page.findViewById<SwipeActionLayout>(R.id.swipe_layout)
        val webContainer = page.findViewById<WebContainer>(R.id.web_container)
        val constraintLayout = page.findViewById<ConstraintLayout>(R.id.constraint_layout)
        constraintLayout.elevation = elevation
        val outlineProvider = WindowOutlineProvider.attachToView(constraintLayout)
        outlineProvider.update {
            outlineTopPadding = webContainer.top * faction.coerceAtLeast(0F)
            outlineBottomPadding =
                (constraintLayout.height - webContainer.bottom) * faction.coerceAtLeast(0F)
            outlineCornerRadius = cornerRadius * faction
        }
        when (faction) {
            0F -> {
                if (!webContainer.touchable) {
                    webContainer.touchable = true
                }
                if (swipeActionLayout.draggable) {
                    swipeActionLayout.draggable = false
                }
                if (viewPager?.isUserInputEnabled == true) {
                    viewPager?.isUserInputEnabled = false
                }
            }
            1F -> {
                if (webContainer.touchable) {
                    webContainer.touchable = false
                }
                if (!swipeActionLayout.draggable) {
                    swipeActionLayout.draggable = true
                }
                if (viewPager?.isUserInputEnabled == false) {
                    viewPager?.isUserInputEnabled = true
                }
            }
            else -> {
                if (webContainer.touchable) {
                    webContainer.touchable = false
                }
                if (swipeActionLayout.draggable) {
                    swipeActionLayout.draggable = false
                }
                if (viewPager?.isUserInputEnabled == true) {
                    viewPager?.isUserInputEnabled = false
                }
            }
        }
    }
}