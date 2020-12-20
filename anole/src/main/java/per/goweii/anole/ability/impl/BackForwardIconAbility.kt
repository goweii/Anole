package per.goweii.anole.ability.impl

import android.view.View
import android.webkit.WebView
import per.goweii.anole.ability.AnoleAbility
import per.goweii.anole.view.AnoleView

class BackForwardIconAbility(
    private val backView: View?,
    private val forwardView: View?
): AnoleAbility() {
    override fun onAttachToWebView(anoleView: AnoleView) {
        super.onAttachToWebView(anoleView)
        backView?.changeEnable(anoleView.canGoBackOrForward(-1))
        forwardView?.changeEnable(anoleView.canGoBackOrForward(1))
    }

    override fun doUpdateVisitedHistory(
        view: WebView,
        url: String,
        isReload: Boolean
    ): Boolean {
        backView?.changeEnable(view.canGoBack())
        forwardView?.changeEnable(view.canGoForward())
        return super.doUpdateVisitedHistory(view, url, isReload)
    }

    private fun View.changeEnable(enable: Boolean) {
        if (enable) {
            alpha = 1F
            isEnabled = true
        } else {
            alpha = 0.5F
            isEnabled = false
        }
    }
}