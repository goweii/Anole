package per.goweii.android.anole.utils

import android.app.Application
import android.content.Context
import android.util.SparseArray
import androidx.annotation.UiThread
import androidx.core.util.containsKey
import per.goweii.anole.WebFactory
import per.goweii.anole.ability.impl.*
import per.goweii.anole.kernel.system.SystemWebInstanceBuilder
import per.goweii.anole.view.KernelView

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

    private val kernels = SparseArray<KernelView>()

    init {
        WebFactory.setInstanceBuilder(SystemWebInstanceBuilder())
    }

    fun get(kernelId: Int): KernelView {
        var kernelView = kernels.get(kernelId)
        if (kernelView != null) {
            kernels.remove(kernelId)
        } else {
            kernelView = create()
        }
        return kernelView
    }

    fun remove(kernelId: Int): KernelView? {
        val kernelView = kernels.get(kernelId)
        kernels.remove(kernelId)
        return kernelView
    }

    fun put(kernelId: Int, kernelView: KernelView) {
        if (kernels.containsKey(kernelId)) {
            throw IllegalStateException("已存在相同kernelId($kernelId)的KernelView")
        }
        kernels.put(kernelId, kernelView)
    }

    fun create(): KernelView {
        return WebFactory.with(application).get().apply {
            webClient.addAbility(FullscreenVideoAbility())
            webClient.addAbility(DownloadAbility())
            webClient.addAbility(AppOpenAbility())
            webClient.addAbility(FileChooseAbility())
            webClient.addAbility(ConsoleAbility())
            webClient.addAbility(PermissionAbility())
            webClient.addAbility(WindowAbility())
        }
    }
}