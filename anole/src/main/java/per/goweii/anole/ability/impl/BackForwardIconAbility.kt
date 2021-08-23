package per.goweii.anole.ability.impl

import android.view.View
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.WebKernel

class BackForwardIconAbility(
    private val backView: View?,
    private val forwardView: View?
) : WebAbility() {
    private var kernel: WebKernel? = null

    override fun onAttachToKernel(kernel: WebKernel) {
        super.onAttachToKernel(kernel)
        this.kernel = kernel
        backView?.changeEnable(kernel.canGoBackOrForward(-1))
        forwardView?.changeEnable(kernel.canGoBackOrForward(1))
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
        backView?.changeEnable(kernel?.canGoBack ?: false)
        forwardView?.changeEnable(kernel?.canGoForward ?: false)
        return super.doUpdateVisitedHistory(webView, url, isReload)
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