package per.goweii.android.anole.web

import android.graphics.Outline
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.annotation.FloatRange
import androidx.viewpager2.widget.ViewPager2
import per.goweii.android.anole.R

class WebPageTransformer(
    @FloatRange(from = 0.0, to = 1.0) val scale: Float,
    private val elevation: Float,
    private val gap: Float,
    private val cornerRadius: Float
) : ViewPager2.PageTransformer {
    private val outlineProvider = object : ViewOutlineProvider() {
        override fun getOutline(view: View, outline: Outline) {
            outline.setRoundRect(
                0,
                0,
                view.width,
                view.height,
                cornerRadius * faction
            )
        }
    }

    var faction: Float = 0F
        set(value) {
            field = value.coerceIn(0F, 1F)
        }

    override fun transformPage(page: View, position: Float) {
        val view = page.findViewById<View>(R.id.web_container)
        if (view.outlineProvider !== outlineProvider) {
            view.clipToOutline = true
            view.outlineProvider = outlineProvider
            var viewGroup = view.parent as? ViewGroup?
            while (viewGroup != null && viewGroup !is ViewPager2) {
                viewGroup.clipToOutline = false
                viewGroup.clipChildren = false
                viewGroup.clipToPadding = false
                viewGroup = viewGroup.parent as? ViewGroup?
            }
        }
        view.invalidateOutline()
        view.elevation = elevation * faction

        val scale = 1F - (1F - scale) * faction
        page.pivotX = page.width / 2F
        page.pivotY = page.height / 2F
        page.scaleX = scale
        page.scaleY = scale
        page.translationX = -(page.width - (page.width * scale) - gap) * position
    }
}