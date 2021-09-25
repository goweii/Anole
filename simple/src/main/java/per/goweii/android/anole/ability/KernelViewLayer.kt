package per.goweii.android.anole.ability

import android.animation.Animator
import android.animation.AnimatorSet
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import per.goweii.anole.view.KernelView
import per.goweii.layer.core.FrameLayer
import per.goweii.layer.core.anim.AnimatorHelper
import per.goweii.layer.dialog.ContainerLayout

class KernelViewLayer(
    kernelView: KernelView,
    private val contentView: View
) : FrameLayer(kernelView) {
    private val context get() = viewHolder.root.context
    private lateinit var backgroundView: View

    init {
        isInterceptKeyEvent = true
        isCancelableOnKeyBack = true
    }

    override fun onCreateChild(inflater: LayoutInflater, parent: ViewGroup): View {
        val container = ContainerLayout(context)
        backgroundView = View(context).apply {
            setBackgroundColor(ColorUtils.setAlphaComponent(Color.BLACK, 255 / 2))
            alpha = 0F
            isClickable = true
        }
        backgroundView.setOnClickListener { dismiss() }
        container.addView(
            backgroundView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        contentView.isClickable = true
        container.addView(
            contentView, contentView.layoutParams ?: ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        )
        return container
    }

    override fun onCreateInAnimator(view: View): Animator {
        val set = AnimatorSet()
        set.playTogether(
            AnimatorHelper.createAlphaInAnim(backgroundView),
            AnimatorHelper.createZoomAlphaInAnim(contentView)
        )
        return set
    }

    override fun onCreateOutAnimator(view: View): Animator {
        val set = AnimatorSet()
        set.playTogether(
            AnimatorHelper.createAlphaOutAnim(backgroundView),
            AnimatorHelper.createZoomAlphaOutAnim(contentView)
        )
        return set
    }
}