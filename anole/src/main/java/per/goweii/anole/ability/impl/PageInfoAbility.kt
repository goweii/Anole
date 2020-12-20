package per.goweii.anole.ability.impl

import android.graphics.Bitmap
import android.webkit.WebView
import per.goweii.anole.ability.AnoleAbility
import per.goweii.anole.view.AnoleView

class PageInfoAbility(
    private val onReceivedPageTitle: (PageInfoAbility.(String?) -> Unit)? = null,
    private val onReceivedPageIcon: (PageInfoAbility.(Bitmap?) -> Unit)? = null
): AnoleAbility() {

    override fun onAttachToWebView(anoleView: AnoleView) {
        super.onAttachToWebView(anoleView)
        onReceivedPageTitle?.invoke(this, anoleView.title)
        onReceivedPageIcon?.invoke(this, anoleView.favicon)
    }

    override fun onReceivedTitle(view: WebView, title: String?): Boolean {
        onReceivedPageTitle?.invoke(this, title)
        return super.onReceivedTitle(view, title)
    }

    override fun onReceivedIcon(view: WebView, icon: Bitmap?): Boolean {
        onReceivedPageIcon?.invoke(this, icon)
        return super.onReceivedIcon(view, icon)
    }
}