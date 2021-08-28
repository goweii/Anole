package per.goweii.anole.ability.impl

import android.graphics.Bitmap
import android.view.View
import per.goweii.anole.ability.WebAbility

class ProgressAbility(
    private val onProgress: ((Int) -> Unit)? = null
) : WebAbility() {
    private val dismissRunnable = Runnable {
        onProgress?.invoke(-1)
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
        val p = when {
            newProgress >= 80 -> 100
            else -> newProgress
        }
        update(webView, p)
        return super.onProgressChanged(webView, newProgress)
    }

    private fun update(webView: View, p: Int) {
        onProgress?.invoke(p)
        if (p == 100) {
            webView.removeCallbacks(dismissRunnable)
            webView.postDelayed(dismissRunnable, 200)
        }
    }
}