package per.goweii.android.anole.utils

import android.app.Application
import android.content.Context
import android.util.SparseArray
import androidx.annotation.UiThread
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

    var onCreateWindow: ((kernel: WebKernel) -> Unit)? = null
    var onCloseWindow: ((kernel: WebKernel) -> Unit)? = null

    init {
        val systemWebInstanceBuilder = SystemWebInstanceBuilder()
        systemWebInstanceBuilder.onCreateWindow = {
            kernels.put(it.hashCode(), it)
            onCreateWindow?.invoke(it)
        }
        systemWebInstanceBuilder.onCloseWindow = {
            onCloseWindow?.invoke(it)
            kernels.remove(it.hashCode())
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
            .registerAbility(CustomErrorPageAbility("errorpages/error.html"))
            .get()
    }
}