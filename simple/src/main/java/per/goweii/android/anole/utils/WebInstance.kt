package per.goweii.android.anole.utils

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.SparseArray
import androidx.annotation.UiThread
import androidx.core.content.ContextCompat
import per.goweii.android.anole.R
import per.goweii.anole.WebFactory
import per.goweii.anole.WebInstanceInitializer
import per.goweii.anole.ability.impl.*
import per.goweii.anole.kernel.WebKernel
import per.goweii.anole.kernel.system.SystemWebInstanceCreator
import per.goweii.anole.utils.UserAgent

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

    var onCreateWindow: ((webToken: WebToken) -> Unit)? = null
    var onCloseWindow: ((kernelId: Int) -> Unit)? = null

    init {
        val systemWebInstanceBuilder = SystemWebInstanceCreator()
        systemWebInstanceBuilder.onCreateWindow = ::onCreateWindow
        systemWebInstanceBuilder.onCloseWindow = ::onCloseWindow
        WebFactory.setInstanceBuilder(systemWebInstanceBuilder)
        WebFactory.addInstanceInitializer(InstanceInitializer())
    }

    fun obtain(kernelId: Int): WebKernel {
        var kernelView = kernels.get(kernelId)
        if (kernelView == null) {
            kernelView = WebFactory.create(application)
            kernels.put(kernelId, kernelView)
        }
        return kernelView
    }

    fun release(kernelId: Int): WebKernel? {
        val kernelView = kernels.get(kernelId)
        kernels.remove(kernelId)
        return kernelView
    }

    private fun onCreateWindow(kernel: WebKernel, parentKernel: WebKernel) {
        val parentKernelId = kernels.indexOfValue(parentKernel)
            .takeIf { it >= 0 }
            ?.let { kernels.keyAt(it) }
        WebToken(null, parentKernelId = parentKernelId).let {
            kernels.put(it.kernelId, kernel)
            onCreateWindow?.invoke(it)
        }
    }

    private fun onCloseWindow(kernel: WebKernel) {
        kernels.indexOfValue(kernel)
            .takeIf { it >= 0 }
            ?.let { kernels.keyAt(it) }
            ?.let { onCloseWindow?.invoke(it) }
    }

    private inner class InstanceInitializer : WebInstanceInitializer {
        override fun initialize(webKernel: WebKernel) {
            webKernel.apply {
                val context = webKernel.kernelView.context.applicationContext
                val pm = context.packageManager
                val appName = pm.getApplicationLabel(context.applicationInfo).toString()
                val appVersionName = pm.getPackageInfo(context.packageName, 0).versionName
                val android = "Android ${Build.VERSION.RELEASE}"
                val sdk = "SDK ${Build.VERSION.SDK_INT}"
                val model = "MODEL ${Build.MODEL}"
                webKernel.settings.userAgentString =
                    UserAgent.from(webKernel.settings.userAgentString ?: "")
                        .append(appName, appVersionName, android, sdk, model)
                        .toString()

                webClient.addAbility(FullscreenVideoAbility())
                webClient.addAbility(DownloadAbility())
                webClient.addAbility(AppOpenAbility())
                webClient.addAbility(FileChooseAbility())
                webClient.addAbility(ConsoleAbility())
                webClient.addAbility(PermissionAbility())

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
}