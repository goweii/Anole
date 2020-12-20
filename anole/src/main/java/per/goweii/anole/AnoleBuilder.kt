package per.goweii.anole

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import per.goweii.anole.ability.AnoleAbility
import per.goweii.anole.ability.impl.*
import per.goweii.anole.view.AnoleView

/**
 * 创建WebView实例
 */
@SuppressLint("SetJavaScriptEnabled")
class AnoleBuilder(context: Context) {
    companion object {
        fun with(context: Context): AnoleBuilder {
            return AnoleBuilder(context)
        }
    }

    private val mAnoleView = AnoleView(context)

    fun get(lifecycleOwner: LifecycleOwner?): AnoleView {
        mAnoleView.bindToLifecycle(lifecycleOwner)
        return mAnoleView
    }

    fun attachTo(parent: FrameLayout) = apply {
        parent.addView(mAnoleView, parent.childCount, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ))
    }

    fun applyDefaultConfig() = apply {
        appendDefUserAgent()
        forceDarkAuto()
        addClient(FullscreenVideoAbility())
        addClient(DownloadAbility())
        addClient(AppOpenAbility())
        addClient(FileChooseAbility())
        addClient(ConsoleAbility())
        addClient(PermissionAbility())
        addClient(WindowAbility())
    }

    fun addClient(client: AnoleAbility) = apply {
        this.mAnoleView.client.addAbility(client)
    }

    fun forceDarkAuto() = apply {
        mAnoleView.forceDarkAuto()
    }

    fun forceDark(dark: Boolean) = apply {
        mAnoleView.forceDark(dark)
    }

    fun appendUserAgent(userAgent: String) = apply {
        mAnoleView.appendUserAgent(userAgent)
    }

    fun appendUserAgent(appName: String, appVersionName: String, vararg extInfo: String) = apply {
        mAnoleView.appendUserAgent(appName, appVersionName, *extInfo)
    }

    fun appendDefUserAgent() = apply {
        mAnoleView.appendDefUserAgent()
    }

}