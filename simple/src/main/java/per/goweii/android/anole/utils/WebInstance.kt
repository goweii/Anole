package per.goweii.android.anole.utils

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.SparseArray
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import per.goweii.android.anole.R
import per.goweii.anole.WebFactory
import per.goweii.anole.ability.impl.*
import per.goweii.anole.kernel.WebKernel
import per.goweii.anole.kernel.system.SystemWebInstanceBuilder

/**
 * Web实例管理，缓存KernelView，用于Fragment重建时恢复KernelView
 */
class WebInstance(private val application: Application) {
    companion object {
        private var sInstance: WebInstance? = null

        @UiThread
        fun getInstance(context: Context): WebInstance {
            if (sInstance == null) {
                sInstance = WebInstance(context.applicationContext as Application)
            }
            return sInstance!!
        }
    }

    private val kernels = SparseArray<WebKernel>()

    var onCreateWindow: ((kernelId: Int) -> Unit)? = null
    var onCloseWindow: ((kernelId: Int) -> Unit)? = null

    init {
        val systemWebInstanceBuilder = SystemWebInstanceBuilder()
        systemWebInstanceBuilder.onCreateWindow = { kernel ->
            WebToken(null, true).let {
                kernels.put(it.kernelId, kernel)
                onCreateWindow?.invoke(it.kernelId)
            }
        }
        systemWebInstanceBuilder.onCloseWindow = { kernel ->
            kernels.indexOfValue(kernel)
                .takeIf { it >= 0 }
                ?.let { kernels.keyAt(it) }
                ?.let { onCloseWindow?.invoke(it) }
        }
        WebFactory.setInstanceBuilder(systemWebInstanceBuilder)
    }

    fun obtain(kernelId: Int): WebKernel {
        var kernelView = kernels.get(kernelId)
        if (kernelView == null) {
            kernelView = create()
            kernels.put(kernelId, kernelView)
        }
        return kernelView
    }

    fun release(kernelId: Int): WebKernel? {
        val kernelView = kernels.get(kernelId)
        kernels.remove(kernelId)
        return kernelView
    }

    private fun create(): WebKernel {
        return WebFactory.with(application, null)
            .appendDefUserAgent()
            .registerAbility(FullscreenVideoAbility())
            .registerAbility(DownloadAbility())
            .registerAbility(AppOpenAbility())
            .registerAbility(FileChooseAbility())
            .registerAbility(ConsoleAbility())
            .registerAbility(PermissionAbility())
            .get()
            .apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val drawable = ContextCompat.getDrawable(application, R.drawable.scrollbar)
                    webView.scrollBarSize = drawable!!.intrinsicWidth
                    webView.isVerticalScrollBarEnabled = true
                    webView.isHorizontalScrollBarEnabled = true
                    webView.isScrollbarFadingEnabled = true
                    webView.isNestedScrollingEnabled = true
                    webView.verticalScrollbarThumbDrawable = drawable
                    webView.horizontalScrollbarThumbDrawable = drawable
                    webView.scrollBarFadeDuration = 300
                }
            }
    }
}