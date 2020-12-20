package per.goweii.anole.ability.impl

import android.graphics.Bitmap
import android.os.Build
import android.view.View
import android.webkit.WebView
import android.widget.ProgressBar
import per.goweii.anole.ability.AnoleAbility

class ProgressAbility(
    private val progressBar: ProgressBar
) : AnoleAbility() {
    private val isShown: Boolean
        get() = progressBar.visibility == View.VISIBLE

    private var dismissRunnable: Runnable? = null

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?): Boolean {
        update(0)
        return super.onPageStarted(view, url, favicon)
    }

    override fun onPageFinished(view: WebView, url: String?): Boolean {
        update(100)
        return super.onPageFinished(view, url)
    }

    override fun onProgressChanged(view: WebView, newProgress: Int): Boolean {
        val p = when {
            newProgress >= 80 -> 100
            else -> newProgress
        }
        update(p)
        return super.onProgressChanged(view, newProgress)
    }

    private fun update(p: Int) {
        progressBar.max = 100
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && p > progressBar.progress) {
            progressBar.setProgress(p, true)
        } else {
            progressBar.progress = p
        }
        if (p == 100) {
            hide()
        } else {
            show()
        }
    }

    private fun show() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dismissRunnable?.let {
                progressBar.removeCallbacks(dismissRunnable)
                dismissRunnable = null
            }
        }
        if (isShown) return
        progressBar.visibility = View.VISIBLE
    }

    private fun hide() {
        if (!isShown) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            dismissRunnable?.let { return }
            dismissRunnable = Runnable {
                progressBar.visibility = View.GONE
                dismissRunnable = null
            }
            progressBar.postDelayed(dismissRunnable, 200)
        } else {
            progressBar.visibility = View.GONE
        }
    }
}