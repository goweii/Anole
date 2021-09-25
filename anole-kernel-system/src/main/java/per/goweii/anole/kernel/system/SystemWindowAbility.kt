package per.goweii.anole.kernel.system

import android.os.Message
import android.view.View
import android.webkit.WebView
import android.webkit.WebView.WebViewTransport
import per.goweii.anole.WebFactory
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.WebKernel
import per.goweii.anole.kernel.WebKernel.Companion.webKernel

class SystemWindowAbility(
    private val onCreateWindow: (
        kernel: WebKernel,
        isDialog: Boolean,
        isUserGesture: Boolean
    ) -> Unit,
    private val onCloseWindow: (
        kernel: WebKernel
    ) -> Unit
) : WebAbility() {

    override fun onCreateWindow(
        webView: View,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean {
        val kernel = kernel ?: return false
        val newKernel = WebFactory.create(kernel.kernelView.context)
        onCreateWindow.invoke(newKernel, isDialog, isUserGesture)
        val transport = resultMsg.obj as WebViewTransport
        transport.webView = newKernel.webView as? WebView?
        resultMsg.sendToTarget()
        return true
    }

    override fun onCloseWindow(webView: View): Boolean {
        kernel ?: return false
        val newKernel = webView.webKernel ?: return false
        onCloseWindow.invoke(newKernel)
        return true
    }

}