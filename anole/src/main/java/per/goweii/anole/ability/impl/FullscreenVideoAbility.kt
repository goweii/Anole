package per.goweii.anole.ability.impl

import android.app.Activity
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import per.goweii.anole.ability.CustomViewCallback
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.utils.findActivity

class FullscreenVideoAbility(
    private val activity: Activity? = null
) : WebAbility() {
    private var view: View? = null
    private var callback: CustomViewCallback? = null
    private var oldOrientation = 0
    private var oldIsFullscreen = false
    private var oldIsKeepScreen = false

    override fun onShowCustomView(
        customView: View?,
        callback: CustomViewCallback?
    ): Boolean {
        hide()
        show(customView, callback)
        return true
    }

    override fun onHideCustomView(): Boolean {
        hide()
        return true
    }

    private fun show(view: View?, callback: CustomViewCallback?) {
        val activity = this.activity ?: view?.findActivity()
        if (activity?.window == null) {
            callback?.onCustomViewHidden()
            return
        }
        view!!.background = ColorDrawable(Color.BLACK)
        val decorView = activity.window.decorView
        decorView as FrameLayout
        decorView.addView(
            view, decorView.childCount, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        oldOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        activity.window.attributes.flags.let {
            oldIsFullscreen = it and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
            oldIsKeepScreen = it and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON != 0
        }
        if (!oldIsFullscreen) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        if (!oldIsKeepScreen) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
        this.view = view
        this.callback = callback
    }

    private fun hide() {
        callback?.onCustomViewHidden()
        val view = this.view ?: return
        view.parent?.let { p ->
            p as ViewGroup
            p.removeView(view)
        }
        this.view = null
        this.callback = null
        val activity = this.activity ?: view.findActivity() ?: return
        activity.requestedOrientation = oldOrientation
        val window = activity.window ?: return
        if (!oldIsFullscreen) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        if (!oldIsKeepScreen) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }
}