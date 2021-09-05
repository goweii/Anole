package per.goweii.android.anole.utils

import android.app.Application
import android.content.Context
import android.util.SparseArray
import androidx.annotation.UiThread
import per.goweii.anole.WebFactory
import per.goweii.anole.view.KernelView

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

    fun get(kernelId: Int): KernelView {
        var kernelView = kernels.get(kernelId)
        if (kernelView != null) {
            kernels.remove(kernelId)
        } else {
            kernelView = create()
        }
        return kernelView
    }

    fun create(): KernelView {
        return WebFactory.with(application)
            .applyDefaultConfig()
            .get()
    }
}