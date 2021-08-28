package per.goweii.anole.ability.impl

import android.graphics.Bitmap
import android.view.View
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.WebKernel

class PageInfoAbility(
    private val onReceivedPageUrl: ((String?) -> Unit)? = null,
    private val onReceivedPageTitle: ((String?) -> Unit)? = null,
    private val onReceivedPageIcon: ((Bitmap?) -> Unit)? = null
) : WebAbility() {

    override fun onAttachToKernel(kernel: WebKernel) {
        super.onAttachToKernel(kernel)
        onReceivedPageUrl?.invoke(kernel.url)
        onReceivedPageTitle?.invoke(kernel.title)
        onReceivedPageIcon?.invoke(kernel.favicon)
    }

    override fun onPageStarted(webView: View, url: String?, favicon: Bitmap?): Boolean {
        onReceivedPageUrl?.invoke(url)
        onReceivedPageTitle?.invoke(url)
        onReceivedPageIcon?.invoke(favicon)
        return super.onPageStarted(webView, url, favicon)
    }

    override fun onReceivedTitle(webView: View, title: String?): Boolean {
        onReceivedPageTitle?.invoke(title)
        return super.onReceivedTitle(webView, title)
    }

    override fun onReceivedIcon(webView: View, icon: Bitmap?): Boolean {
        onReceivedPageIcon?.invoke(icon)
        return super.onReceivedIcon(webView, icon)
    }
}