package per.goweii.anole.kernel.system

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.TextUtils
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import per.goweii.anole.BuildConfig
import per.goweii.anole.WebInstanceCreator
import per.goweii.anole.kernel.WebKernel
import per.goweii.anole.kernel.WebSettings
import per.goweii.anole.utils.currentProcessName

open class SystemWebInstanceCreator : WebInstanceCreator {
    companion object {
        private var hasSetDataDirectorySuffix = false
    }

    var onCreateWindow: ((kernel: WebKernel, parentKernel: WebKernel) -> Unit)? = null
    var onCloseWindow: ((kernel: WebKernel) -> Unit)? = null

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    protected open fun isWebContentsDebuggingEnabled(): Boolean {
        return BuildConfig.DEBUG && TextUtils.equals(BuildConfig.BUILD_TYPE, "debug")
    }

    @RequiresApi(Build.VERSION_CODES.P)
    protected open fun dataDirectorySuffix(context: Context): String? {
        val currProcessName = context.currentProcessName
        if (!currProcessName.isNullOrBlank()) {
            return "${currProcessName}_"
        }
        return null
    }

    override fun build(context: Context): WebKernel {
        return SystemKernelView(context).apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(isWebContentsDebuggingEnabled())
            }
            if (!hasSetDataDirectorySuffix && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val currProcessName = context.currentProcessName
                if (!TextUtils.equals(currProcessName, context.packageName)) {
                    val suffix = dataDirectorySuffix(context)
                    if (!suffix.isNullOrBlank()) {
                        WebView.setDataDirectorySuffix(suffix)
                        hasSetDataDirectorySuffix = true
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                //CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            }
            isLongClickable = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                background = ColorDrawable(Color.TRANSPARENT)
            } else {
                setBackgroundColor(Color.TRANSPARENT)
            }
            overScrollMode = FrameLayout.OVER_SCROLL_NEVER
            //5.0以上开启混合模式加载
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MixedContentMode.MIXED_CONTENT_ALWAYS_ALLOW
            }
            settings.loadWithOverviewMode = true
            settings.useWideViewPort = true
            //允许js代码
            settings.javaScriptEnabled = true
            //允许SessionStorage/LocalStorage存储
            settings.domStorageEnabled = true
            //禁用放缩
            settings.displayZoomControls = false
            settings.builtInZoomControls = false
            //禁用文字缩放
            settings.textZoom = 100
            //允许缓存，设置缓存位置
            settings.cacheMode = WebSettings.CacheMode.LOAD_DEFAULT
            settings.appCacheEnabled = true
            //允许WebView使用File协议
            settings.allowFileAccess = true
            //不保存密码
            settings.savePassword = false
            //自动加载图片
            settings.loadsImagesAutomatically = true
            settings.layoutAlgorithm = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING
            } else {
                WebSettings.LayoutAlgorithm.NARROW_COLUMNS
            }
            settings.supportZoom = false
            settings.geolocationEnabled = true
            settings.supportMultipleWindows = true
            settings.javaScriptCanOpenWindowsAutomatically = true

            webClient.addAbility(SystemWindowAbility(
                onCreateWindow = { kernel, _, _ ->
                    onCreateWindow?.invoke(kernel, this)
                },
                onCloseWindow = { kernel ->
                    onCloseWindow?.invoke(kernel)
                }
            ))
        }
    }
}