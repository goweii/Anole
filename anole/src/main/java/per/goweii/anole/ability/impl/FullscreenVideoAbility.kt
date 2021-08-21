package per.goweii.anole.ability.impl

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.widget.FrameLayout
import per.goweii.anole.ability.WebAbility

class FullscreenVideoAbility(
    private val activity: Activity? = null
) : WebAbility() {
    private var view: View? = null
    private var callback: WebChromeClient.CustomViewCallback? = null
    private var oldOrientation = 0
    private var oldIsFullscreen = false

    override fun onShowCustomView(
        view: View?,
        callback: WebChromeClient.CustomViewCallback?
    ): Boolean {
        hide()
        show(view, callback)
        return true
    }

    override fun onHideCustomView(): Boolean {
        hide()
        return true
    }

    private fun show(view: View?, callback: WebChromeClient.CustomViewCallback?) {
        view ?: return
        val activity = this.activity ?: findActivityFromContext(view.context) ?: return
        val decorView = activity.window?.decorView ?: return
        decorView as FrameLayout
        decorView.addView(
            view, decorView.childCount, FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        oldOrientation = activity.requestedOrientation
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        oldIsFullscreen = activity.window.attributes.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN != 0
        if (!oldIsFullscreen) {
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        this.view = view
        this.callback = callback
    }

    private fun hide() {
        val view = this.view ?: return
        view.parent?.let { p ->
            p as ViewGroup
            p.removeView(view)
        }
        callback?.onCustomViewHidden()
        val activity = this.activity ?: findActivityFromContext(view.context) ?: return
        activity.requestedOrientation = oldOrientation
        if (!oldIsFullscreen) {
            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
        this.view = null
        this.callback = null
    }

    private fun findActivityFromContext(context: Context): Activity? {
        var activity: Activity? = null
        if (context is Activity) {
            activity = context
        } else {
            if (context is ContextWrapper) {
                val baseContext = context.baseContext
                if (baseContext is Activity) {
                    activity = baseContext
                }
            }
        }
        return activity
    }
}