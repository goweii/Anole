package per.goweii.android.anole.web

import android.graphics.Outline
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.FloatRange
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.viewpager2.widget.ViewPager2
import per.goweii.android.anole.R
import per.goweii.android.anole.widget.SwipeActionLayout
import per.goweii.android.anole.widget.WebContainer

class WebPageTransformer(
    private val viewPager: ViewPager2,
    @FloatRange(from = 0.0, to = 1.0)
    val scale: Float,
    private val elevation: Float,
    private val gap: Float,
    private val cornerRadius: Float
) : ViewPager2.PageTransformer {
    private var topMargin = 0
    private var bottomMargin = 0
    private val outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(
                0,
                (topMargin * faction).toInt(),
                view.width,
                view.height - (bottomMargin * faction).toInt(),
                cornerRadius * faction
            )
        }
    }

    var faction: Float = 0F
        set(value) {
            field = value.coerceIn(0F, 1F)
        }

    override fun transformPage(page: View, position: Float) {
        Log.d("transformPage", "position=$position")
        val scale = 1F - (1F - scale) * faction
        page.pivotX = page.width / 2F
        page.pivotY = page.height / 2F
        page.scaleX = scale
        page.scaleY = scale
        page.translationX = -(page.width - (page.width * scale) - gap) * position
        val swipeActionLayout = page.findViewById<SwipeActionLayout>(R.id.swipe_layout)
        val webContainer = page.findViewById<WebContainer>(R.id.web_container)
        val motionLayout = page.findViewById<MotionLayout>(R.id.motion_layout)
        topMargin = webContainer.top
        bottomMargin = motionLayout.height - webContainer.bottom
        if (motionLayout.outlineProvider !== outlineProvider) {
            motionLayout.clipToOutline = true
            motionLayout.outlineProvider = outlineProvider
            var viewGroup = motionLayout.parent as? ViewGroup?
            while (viewGroup != null && viewGroup !is ViewPager2) {
                viewGroup.clipToOutline = false
                viewGroup.clipChildren = false
                viewGroup.clipToPadding = false
                viewGroup = viewGroup.parent as? ViewGroup?
            }
        }
        motionLayout.invalidateOutline()
        motionLayout.elevation = elevation
        when (faction) {
            0F -> {
                if (!webContainer.touchable) {
                    webContainer.touchable = true
                }
                if (swipeActionLayout.draggable) {
                    swipeActionLayout.draggable = false
                }
                if (viewPager.isUserInputEnabled) {
                    viewPager.isUserInputEnabled = false
                }
            }
            1F -> {
                if (webContainer.touchable) {
                    webContainer.touchable = false
                }
                if (!swipeActionLayout.draggable) {
                    swipeActionLayout.draggable = true
                }
                if (!viewPager.isUserInputEnabled) {
                    viewPager.isUserInputEnabled = true
                }
            }
            else -> {
                if (webContainer.touchable) {
                    webContainer.touchable = false
                }
                if (swipeActionLayout.draggable) {
                    swipeActionLayout.draggable = false
                }
                if (viewPager.isUserInputEnabled) {
                    viewPager.isUserInputEnabled = false
                }
            }
        }
    }
}