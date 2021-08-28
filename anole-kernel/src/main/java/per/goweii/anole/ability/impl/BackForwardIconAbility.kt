package per.goweii.anole.ability.impl

import android.view.View
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.WebKernel

class BackForwardIconAbility(
    private val canGoBack: ((Boolean) -> Unit)?,
    private val canGoForward: ((Boolean) -> Unit)?
) : WebAbility() {
    private var kernel: WebKernel? = null

    override fun onAttachToKernel(kernel: WebKernel) {
        super.onAttachToKernel(kernel)
        this.kernel = kernel
        canGoBack?.invoke(kernel.canGoBack)
        canGoForward?.invoke(kernel.canGoForward)
    }

    override fun onDetachFromKernel(kernel: WebKernel) {
        super.onDetachFromKernel(kernel)
        this.kernel = null
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