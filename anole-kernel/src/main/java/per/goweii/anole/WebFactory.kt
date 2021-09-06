package per.goweii.anole

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.utils.UserAgent
import per.goweii.anole.view.KernelView

@SuppressLint("SetJavaScriptEnabled")
class WebFactory(private val kernelView: KernelView) {
    companion object {
        private var instanceBuilder: WebInstanceBuilder<out KernelView>? = null

        fun setInstanceBuilder(instanceBuilder: WebInstanceBuilder<out KernelView>) {
            this.instanceBuilder = instanceBuilder
        }

        fun with(context: Context): WebFactory {
            if (instanceBuilder == null) {
                throw NullPointerException("instanceBuilder == null")
            }
            val kernelView = instanceBuilder!!.build(context)
            return WebFactory(kernelView)
        }
    }

    fun get(): KernelView {
        return kernelView
    }

    fun bindToLifecycle(lifecycleOwner: LifecycleOwner?) = apply {
        kernelView.bindToLifecycle(lifecycleOwner)
    }

    fun attachTo(parent: FrameLayout) = apply {
        parent.addView(
            kernelView, parent.childCount, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }

    fun registerAbility(client: WebAbility) = apply {
        this.kernelView.webClient.addAbility(client)
    }

    fun userAgentString(userAgent: String) = apply {
        kernelView.settings.userAgentString = userAgent
    }

    fun appendUserAgent(appName: String, appVersionName: String, vararg extInfo: String) = apply {
        kernelView.settings.userAgentString =
            UserAgent.from(kernelView.settings.userAgentString ?: "")
                .append(appName, appVersionName, *extInfo)
                .toString()
    }

    fun appendDefUserAgent() = apply {
        val context = kernelView.context.applicationContext
        val pm = context.packageManager
        val appName = pm.getApplicationLabel(context.applicationInfo).toString()
        val appVersionName = pm.getPackageInfo(context.packageName, 0).versionName
        val android = "Android ${Build.VERSION.RELEASE}"
        val sdk = "SDK ${Build.VERSION.SDK_INT}"
        val model = "MODEL ${Build.MODEL}"
        kernelView.settings.userAgentString =
            UserAgent.from(kernelView.settings.userAgentString ?: "")
                .append(appName, appVersionName, android, sdk, model)
                .toString()
    }

}