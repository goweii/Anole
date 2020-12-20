package per.goweii.anole.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.webkit.WebSettingsCompat
import androidx.webkit.WebViewFeature
import per.goweii.anole.BuildConfig
import per.goweii.anole.R
import per.goweii.anole.ability.impl.ProgressAbility
import per.goweii.anole.client.AnoleClient
import per.goweii.anole.client.BridgeWebChromeClient
import per.goweii.anole.client.BridgeWebViewClient
import per.goweii.anole.client.MixedWebClient
import per.goweii.anole.utils.UserAgent

/**
 * 显示WebView及进度条
 */
class AnoleView : FrameLayout {
    private var mLifecycleObserver: LifecycleObserver = AnoleLifecycleObserver()
    private var mLifecycleOwner: LifecycleOwner? = null
    private val mAnoleClient = AnoleClient(this)

    val client: AnoleClient
        get() = mAnoleClient
    val bindingLifecycleOwner: LifecycleOwner?
        get() = mLifecycleOwner

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
    )

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(
            context: Context,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    val progressBar: ProgressBar = LayoutInflater.from(context).inflate(
            R.layout.anole_progressbar, this, false
    ) as ProgressBar
    val webView: WebView = NestedWebView(context)

    val url: String? get() = webView.url
    val title: String? get() = webView.title
    val favicon: Bitmap? get() = webView.favicon

    init {
        addView(webView, 0, LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        ))
        addView(progressBar)
        applyDefaultSettings()
        client.addAbility(ProgressAbility(progressBar))
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun applyDefaultSettings() {
        webView.apply {
            if (BuildConfig.DEBUG) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    WebView.setWebContentsDebuggingEnabled(true)
                }
            }
            isLongClickable = true
            background = ColorDrawable(Color.TRANSPARENT)
            overScrollMode = OVER_SCROLL_NEVER
            //5.0以上开启混合模式加载
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW;
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
            settings.cacheMode = WebSettings.LOAD_DEFAULT
            settings.setAppCacheEnabled(true)
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
            settings.setSupportZoom(false)
            settings.setGeolocationEnabled(true)
            settings.setSupportMultipleWindows(true)
            settings.javaScriptCanOpenWindowsAutomatically = true
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            }
            setDownloadListener(mAnoleClient)
            val mixedWebClient = MixedWebClient(mAnoleClient)
            webViewClient = BridgeWebViewClient(mixedWebClient)
            webChromeClient = BridgeWebChromeClient(mixedWebClient)
        }
    }

    fun bindToLifecycle(lifecycleOwner: LifecycleOwner?) {
        if (mLifecycleOwner != lifecycleOwner) {
            mLifecycleOwner?.lifecycle?.removeObserver(mLifecycleObserver)
            mLifecycleOwner = lifecycleOwner
            mLifecycleOwner?.lifecycle?.addObserver(mLifecycleObserver)
        }
    }

    fun handleBackPressed(): Boolean {
        if (webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return false
    }

    fun resume() {
        webView.resumeTimers()
        webView.onResume()
    }

    fun pause() {
        webView.pauseTimers()
        webView.onPause()
    }

    fun destroy() {
        mAnoleClient.clearAbilities()
        removeView(webView)
        webView.apply {
            stopLoading()
            settings.javaScriptEnabled = false
            clearView()
            removeAllViews()
            try {
                destroy()
            } catch (e: Throwable) {
            }
        }
    }

    fun forceDarkAuto() = apply {
        try {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                WebSettingsCompat.setForceDark(webView.settings, WebSettingsCompat.FORCE_DARK_AUTO)
            }
        } catch (e: UnsupportedOperationException) {
        }
    }

    fun forceDark(dark: Boolean) = apply {
        try {
            if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
                val flag = if (dark) {
                    WebSettingsCompat.FORCE_DARK_ON
                } else {
                    WebSettingsCompat.FORCE_DARK_OFF
                }
                WebSettingsCompat.setForceDark(webView.settings, flag)
            }
        } catch (e: UnsupportedOperationException) {
        }
    }

    /**
     * Anole/1.0.0 (Android 10.0; SDK 11)
     */
    fun appendUserAgent(userAgent: String) = apply {
        webView.settings.userAgentString = UserAgent.from(webView.settings.userAgentString)
                .append(userAgent)
                .toString()
    }

    /**
     * Anole/1.0.0 (Android 10.0; SDK 11)
     */
    fun appendUserAgent(appName: String, appVersionName: String, vararg extInfo: String) = apply {
        webView.settings.userAgentString = UserAgent.from(webView.settings.userAgentString)
                .append(appName, appVersionName, *extInfo)
                .toString()
    }

    /**
     * Anole/1.0.0 (Android 10.0; SDK 11; MODEL xiaomi)
     */
    fun appendDefUserAgent() = apply {
        val context = context.applicationContext
        val pm = context.packageManager
        val appName = pm.getApplicationLabel(context.applicationInfo).toString()
        val appVersionName = pm.getPackageInfo(context.packageName, 0).versionName
        val android = "Android ${Build.VERSION.RELEASE}"
        val sdk = "SDK ${Build.VERSION.SDK_INT}"
        val model = "MODEL ${Build.MODEL}"
        webView.settings.userAgentString = UserAgent.from(webView.settings.userAgentString)
                .append(appName, appVersionName, android, sdk, model)
                .toString()
    }

    fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    fun goBackOrForward(steps: Int) {
        webView.goBackOrForward(steps)
    }

    fun canGoBackOrForward(steps: Int): Boolean {
        return webView.canGoBackOrForward(steps)
    }

    private inner class AnoleLifecycleObserver : DefaultLifecycleObserver {
        override fun onResume(owner: LifecycleOwner) {
            super.onResume(owner)
            this@AnoleView.resume()
        }

        override fun onPause(owner: LifecycleOwner) {
            super.onPause(owner)
            this@AnoleView.pause()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            this@AnoleView.destroy()
        }
    }

}