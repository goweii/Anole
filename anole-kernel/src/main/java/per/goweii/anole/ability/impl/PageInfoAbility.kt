package per.goweii.anole.ability.impl

import android.graphics.Bitmap
import android.view.View
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.WebKernel

class PageInfoAbility(
    private val onReceivedPageTitle: (PageInfoAbility.(String?) -> Unit)? = null,
    private val onReceivedPageIcon: (PageInfoAbility.(Bitmap?) -> Unit)? = null
) : WebAbility() {

    override fun onAttachToKernel(kernel: WebKernel) {
        super.onAttachToKernel(kernel)
        onReceivedPageTitle?.invoke(this, kernel.title)
        onReceivedPageIcon?.invoke(this, kernel.favicon)
    }

    override fun onReceivedTitle(webView: View, title: String?): Boolean {
        onReceivedPageTitle?.invoke(this, title)
        return super.onReceivedTitle(webView, title)
    }

    override fun onReceivedIcon(webView: View, icon: Bitmap?): Boolean {
        onReceivedPageIcon?.invoke(this, icon)
        return super.onReceivedIcon(webView, icon)
    }
}