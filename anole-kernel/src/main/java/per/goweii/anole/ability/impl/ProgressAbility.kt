package per.goweii.anole.ability.impl

import android.graphics.Bitmap
import android.view.View
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.WebKernel

class ProgressAbility(
    private val onProgress: ((Int) -> Unit)? = null
) : WebAbility() {
    private val finishedProgress = 80
    private val dismissRunnable = Runnable {
        onProgress?.invoke(-1)
    }

    override fun onAttachToKernel(kernel: WebKernel) {
        super.onAttachToKernel(kernel)
        if (kernel.progress >= finishedProgress) {
            onProgress?.invoke(-1)
        } else {
            onProgress?.invoke(kernel.progress)
        }
    }

    override fun onPageStarted(webView: View, url: String?, favicon: Bitmap?): Boolean {
        webView.removeCallbacks(dismissRunnable)
        update(webView, 0)
        return super.onPageStarted(webView, url, favicon)
    }

    override fun onPageFinished(webView: View, url: String?): Boolean {
        update(webView, 100)
        return super.onPageFinished(webView, url)
    }

    override fun onProgressChanged(webView: View, newProgress: Int): Boolean {
        update(webView, newProgress)
        return super.onProgressChanged(webView, newProgress)
    }

    private fun update(webView: View, newProgress: Int) {
        val p = when {
            newProgress >= finishedProgress -> 100
            else -> newProgress
        }
        onProgress?.invoke(p)
        if (p == 100) {
            webView.removeCallbacks(dismissRunnable)
            webView.postDelayed(dismissRunnable, 200)
        }
    }
}