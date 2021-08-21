package per.goweii.anole.ability.impl

import android.os.Message
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebView.WebViewTransport
import android.widget.FrameLayout
import per.goweii.anole.AnoleBuilder
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.view.AnoleView

class WindowAbility : WebAbility() {
    private var anoleView: AnoleView? = null

    override fun onAttachToWebView(anoleView: AnoleView) {
        super.onAttachToWebView(anoleView)
        this.anoleView = anoleView
    }

    override fun onDetachFromWebView(anoleView: AnoleView) {
        this.anoleView = null
        super.onDetachFromWebView(anoleView)
    }

    override fun onCreateWindow(
            view: WebView,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message
    ): Boolean {
        val anoleView = anoleView ?: return false
        var container: FrameLayout? = null
        val parent = anoleView.parent ?: return false
        if (parent is FrameLayout) {
            container = parent
        }
        container ?: return false
        val newAnoleView = AnoleBuilder.with(anoleView.context)
                .applyDefaultConfig()
                .attachTo(container)
                .get(anoleView.bindingLifecycleOwner)
        val transport = resultMsg.obj as WebViewTransport
        transport.webView = newAnoleView.webView
        resultMsg.sendToTarget()
        return true
    }

    override fun onCloseWindow(window: WebView): Boolean {
        val anoleView = anoleView ?: return false
        val parent = anoleView.parent ?: return false
        parent as ViewGroup
        parent.removeView(anoleView)
        anoleView.destroy()
        return true
    }

}