package per.goweii.anole.ability.impl

import android.view.View
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.WebKernel

class BackForwardIconAbility(
    private val canGoBack: ((Boolean) -> Unit)?,
    private val canGoForward: ((Boolean) -> Unit)?
) : WebAbility() {

    override fun onAttachToKernel(kernel: WebKernel) {
        super.onAttachToKernel(kernel)
        canGoBack?.invoke(kernel.canGoBack)
        canGoForward?.invoke(kernel.canGoForward)
    }

    override fun doUpdateVisitedHistory(
        webView: View,
        url: String,
        isReload: Boolean
    ): Boolean {
        canGoBack?.invoke(kernel?.canGoBack ?: false)
        canGoForward?.invoke(kernel?.canGoForward ?: false)
        return super.doUpdateVisitedHistory(webView, url, isReload)
    }
}