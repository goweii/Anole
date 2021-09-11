package per.goweii.android.anole.web

import android.graphics.Outline
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.viewpager2.widget.ViewPager2

class WindowOutlineProvider : ViewOutlineProvider() {
    private var targetView: View? = null
    private var targetViewOldOutlineProvider: ViewOutlineProvider? = null
    private var targetViewOldClipToOutline: Boolean? = null

    var outlineTopPadding = 0F
    var outlineBottomPadding = 0F
    var outlineLeftPadding = 0F
    var outlineRightPadding = 0F
    var outlineCornerRadius = 0F

    companion object {
        fun isAttachedToView(view: View): Boolean {
            val outlineProvider = view.outlineProvider ?: return false
            return outlineProvider is WindowOutlineProvider
        }

        fun attachToView(view: View): WindowOutlineProvider {
            return if (view.outlineProvider is WindowOutlineProvider) {
                view.outlineProvider as WindowOutlineProvider
            } else {
                WindowOutlineProvider().apply { attachToView(view) }
            }
        }
    }

    fun attachToView(view: View) {
        detachFromView()
        targetView = view
        targetViewOldOutlineProvider = view.outlineProvider
        targetViewOldClipToOutline = view.clipToOutline
        if (targetViewOldOutlineProvider !== this) {
            view.clipToOutline = true
            view.outlineProvider = this
            var viewGroup = view.parent as? ViewGroup?
            while (viewGroup != null && viewGroup !is ViewPager2) {
                viewGroup.clipToOutline = false
                viewGroup.clipChildren = false
                viewGroup.clipToPadding = false
                viewGroup = viewGroup.parent as? ViewGroup?
            }
        }
    }

    fun detachFromView() {
        targetView?.outlineProvider = targetViewOldOutlineProvider
        targetViewOldClipToOutline?.let { targetView?.clipToOutline = it }
        targetView = null
        targetViewOldClipToOutline = null
        targetViewOldOutlineProvider = null
    }

    fun update(block: (WindowOutlineProvider.() -> Unit)?) {
        block?.invoke(this)
        targetView?.invalidateOutline()
    }

    override fun getOutline(view: View, outline: Outline) {
        outline.setRoundRect(
            outlineLeftPadding.toInt(),
            outlineTopPadding.toInt(),
            (view.width - outlineRightPadding).toInt(),
            (view.height - outlineBottomPadding).toInt(),
            outlineCornerRadius
        )
    }
}