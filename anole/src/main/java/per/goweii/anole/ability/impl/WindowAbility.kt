package per.goweii.anole.ability.impl

import android.os.Message
import android.view.View
import android.webkit.WebView
import android.webkit.WebView.WebViewTransport
import android.widget.FrameLayout
import per.goweii.anole.WebFactory
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.WebKernel
import per.goweii.anole.view.KernelView

class WindowAbility : WebAbility() {
    private var webKernel: WebKernel? = null

    override fun onAttachToKernel(kernel: WebKernel) {
        super.onAttachToKernel(kernel)
        this.webKernel = kernel
    }

    override fun onDetachFromKernel(kernel: WebKernel) {
        this.webKernel = null
        super.onDetachFromKernel(kernel)
    }

    override fun onCreateWindow(
        webView: View,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean {
        val kernel = webKernel ?: return false
        val kernelView = kernel.kernelView
        val parent = kernelView.parent ?: return false
        if (parent !is FrameLayout) return false
        val webFactory = WebFactory.with(kernelView.context)
            .applyDefaultConfig()
            .attachTo(parent)
        if (kernelView is KernelView) {
            webFactory.bindToLifecycle(kernelView.bindingLifecycleOwner)
        }
        val newKernel = webFactory.get()
        val transport = resultMsg.obj as WebViewTransport
        transport.webView = newKernel.webView as? WebView
        resultMsg.sendToTarget()
        return true
    }

    override fun onCloseWindow(webView: View): Boolean {
        val kernel = webKernel ?: return false
        val parent = kernel.kernelView.parent ?: return false
        if (parent !is FrameLayout) return false
        var newKernelView: KernelView? = null
        for (i in parent.childCount - 1 downTo 0) {
            val child = parent.getChildAt(i)
            if (child is KernelView && child.webView == webView) {
                newKernelView = child
                break
            }
        }
        if (newKernelView == null) return false
        parent.removeView(newKernelView)
        newKernelView.destroy()
        return true
    }

}