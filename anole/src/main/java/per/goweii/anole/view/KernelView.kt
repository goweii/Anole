package per.goweii.anole.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import per.goweii.anole.client.WebClient
import per.goweii.anole.kernel.WebKernel

abstract class KernelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), WebKernel {
    override val kernelView: View get() = this

    override val webClient: WebClient by lazy { WebClient(this) }

    private val lifecycleObserver: LifecycleObserver = KernelLifecycleObserver()

    var bindingLifecycleOwner: LifecycleOwner? = null
        private set

    fun bindToLifecycle(lifecycleOwner: LifecycleOwner?) {
        if (this.bindingLifecycleOwner != lifecycleOwner) {
            this.bindingLifecycleOwner?.lifecycle?.removeObserver(lifecycleObserver)
            this.bindingLifecycleOwner = lifecycleOwner
            this.bindingLifecycleOwner?.lifecycle?.addObserver(lifecycleObserver)
        }
    }

    private inner class KernelLifecycleObserver : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            this@KernelView.onResume()
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            this@KernelView.onPause()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            this@KernelView.destroy()
        }
    }
}