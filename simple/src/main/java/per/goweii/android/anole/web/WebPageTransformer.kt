package per.goweii.android.anole.web

import android.graphics.Outline
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.FloatRange
import androidx.viewpager2.widget.ViewPager2
import per.goweii.android.anole.R
import per.goweii.android.anole.widget.SwipeActionLayout
import per.goweii.android.anole.widget.TouchableFrameLayout

class WebPageTransformer(
    private val viewPager: ViewPager2,
    @FloatRange(from = 0.0, to = 1.0) val scale: Float,
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
        val webContainer = page.findViewById<FrameLayout>(R.id.web_container)
        val touchableFrameLayout = page.findViewById<TouchableFrameLayout>(R.id.touchable_layout)
        topMargin = webContainer.top
        bottomMargin = touchableFrameLayout.height - webContainer.bottom
        if (touchableFrameLayout.outlineProvider !== outlineProvider) {
            touchableFrameLayout.clipToOutline = true
            touchableFrameLayout.outlineProvider = outlineProvider
            var viewGroup = touchableFrameLayout.parent as? ViewGroup?
            while (viewGroup != null && viewGroup !is ViewPager2) {
                viewGroup.clipToOutline = false
                viewGroup.clipChildren = false
                viewGroup.clipToPadding = false
                viewGroup = viewGroup.parent as? ViewGroup?
            }
        }
        touchableFrameLayout.invalidateOutline()
        touchableFrameLayout.elevation = elevation
        if (faction == 0F) {
            if (!touchableFrameLayout.touchable) {
                touchableFrameLayout.touchable = true
            }
            if (swipeActionLayout.draggable) {
                swipeActionLayout.draggable = false
            }
            if (viewPager.isUserInputEnabled) {
                viewPager.isUserInputEnabled = false
            }
        } else {
            if (touchableFrameLayout.touchable) {
                touchableFrameLayout.touchable = false
            }
            if (!swipeActionLayout.draggable) {
                swipeActionLayout.draggable = true
            }
            if (!viewPager.isUserInputEnabled) {
                viewPager.isUserInputEnabled = true
            }
        }
    }
}