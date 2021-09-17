package per.goweii.anole.client

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.view.KeyEvent
import android.view.View
import per.goweii.anole.ability.*
import per.goweii.anole.kernel.DownloadListener
import per.goweii.anole.kernel.ValueCallback
import per.goweii.anole.kernel.WebKernel

class WebClient(private val kernel: WebKernel) : DownloadListener {
    private val abilities = arrayListOf<WebAbility>()

    fun containsAbility(ability: WebAbility): Boolean {
        return abilities.contains(ability)
    }

    fun addAbility(ability: WebAbility) {
        abilities.add(ability)
        ability.onAttachToKernel(kernel)
    }

    fun removeAbility(ability: WebAbility) {
        ability.onDetachFromKernel(kernel)
        abilities.remove(ability)
    }

    fun clearAbilities() {
        abilities.forEach { it.onDetachFromKernel(kernel) }
        abilities.clear()
    }

    fun shouldInterceptRequest(
        webView: View,
        request: WebResourceRequest
    ): WebResourceResponse? {
        abilities.forEach { ability ->
            ability.shouldInterceptRequest(webView, request)?.let { resp ->
                return resp
            }
        }
        return null
    }

    fun shouldOverrideUrlLoading(
        webView: View,
        request: WebResourceRequest
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.shouldOverrideUrlLoading(webView, request)) {
                return true
            }
        }
        return false
    }

    fun shouldOverrideKeyEvent(webView: View, event: KeyEvent): Boolean {
        abilities.forEach { ability ->
            if (ability.shouldOverrideKeyEvent(webView, event)) {
                return true
            }
        }
        return false
    }

    fun onSafeBrowsingHit(
        webView: View,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onSafeBrowsingHit(webView, request, threatType, callback)) {
                return true
            }
        }
        return false
    }

    fun doUpdateVisitedHistory(webView: View, url: String, isReload: Boolean): Boolean {
        abilities.forEach { ability ->
            if (ability.doUpdateVisitedHistory(webView, url, isReload)) {
                return true
            }
        }
        return false
    }

    fun onReceivedError(
        webView: View,
        request: WebResourceRequest?,
        error: WebResourceError?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onReceivedError(webView, request, error)) {
                return true
            }
        }
        return false
    }

    fun onReceivedHttpError(
        webView: View,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onReceivedHttpError(webView, request, errorResponse)) {
                return true
            }
        }
        return false
    }

    fun onReceivedLoginRequest(
        webView: View,
        realm: String?,
        account: String?,
        args: String?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onReceivedLoginRequest(webView, realm, account, args)) {
                return true
            }
        }
        return false
    }

    fun onPageStarted(webView: View, url: String?, favicon: Bitmap?): Boolean {
        abilities.forEach { ability ->
            if (ability.onPageStarted(webView, url, favicon)) {
                return true
            }
        }
        return false
    }

    fun onPageFinished(webView: View, url: String?): Boolean {
        abilities.forEach { ability ->
            if (ability.onPageFinished(webView, url)) {
                return true
            }
        }
        return false
    }

    fun onScaleChanged(webView: View, oldScale: Float, newScale: Float): Boolean {
        return false
    }

    fun onPageCommitVisible(webView: View, url: String?): Boolean {
        return false
    }

    fun onUnhandledKeyEvent(webView: View, event: KeyEvent?): Boolean {
        abilities.forEach { ability ->
            if (ability.onUnhandledKeyEvent(webView, event)) {
                return true
            }
        }
        return false
    }

    fun onReceivedClientCertRequest(webView: View, request: ClientCertRequest?): Boolean {
        abilities.forEach { ability ->
            if (ability.onReceivedClientCertRequest(webView, request)) {
                return true
            }
        }
        return false
    }

    fun onReceivedHttpAuthRequest(
        webView: View,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onReceivedHttpAuthRequest(webView, handler, host, realm)) {
                return true
            }
        }
        return false
    }

    fun onReceivedSslError(webView: View, handler: SslErrorHandler?, error: SslError?): Boolean {
        abilities.forEach { ability ->
            if (ability.onReceivedSslError(webView, handler, error)) {
                return true
            }
        }
        return false
    }

    fun onTooManyRedirects(webView: View, cancelMsg: Message?, continueMsg: Message?): Boolean {
        return false
    }

    fun onFormResubmission(webView: View, dontResend: Message?, resend: Message?): Boolean {
        return false
    }

    fun onLoadResource(webView: View, url: String?): Boolean {
        abilities.forEach { ability ->
            if (ability.onLoadResource(webView, url)) {
                return true
            }
        }
        return false
    }

    fun onRequestFocus(webView: View): Boolean {
        abilities.forEach { ability ->
            if (ability.onRequestFocus(kernel)) {
                return true
            }
        }
        return false
    }

    fun onJsAlert(
        webView: View,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onJsAlert(webView, url, message, result)) {
                return true
            }
        }
        return false
    }

    fun onJsPrompt(
        webView: View,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onJsPrompt(webView, url, message, defaultValue, result)) {
                return true
            }
        }
        return false
    }

    fun onJsConfirm(
        webView: View,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onJsConfirm(webView, url, message, result)) {
                return true
            }
        }
        return false
    }

    fun onJsBeforeUnload(
        webView: View,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onJsBeforeUnload(webView, url, message, result)) {
                return true
            }
        }
        return false
    }

    fun onJsTimeout(): Boolean {
        abilities.forEach { ability ->
            if (ability.onJsTimeout()) {
                return true
            }
        }
        return false
    }

    fun onShowCustomView(
        customView: View?,
        callback: CustomViewCallback?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onShowCustomView(customView, callback)) {
                return true
            }
        }
        return false
    }

    fun onHideCustomView(): Boolean {
        abilities.forEach { ability ->
            if (ability.onHideCustomView()) {
                return true
            }
        }
        return false
    }

    fun onCreateWindow(
        webView: View,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onCreateWindow(webView, isDialog, isUserGesture, resultMsg)) {
                return true
            }
        }
        return false
    }

    fun onCloseWindow(webView: View): Boolean {
        abilities.forEach { ability ->
            if (ability.onCloseWindow(webView)) {
                return true
            }
        }
        return false
    }

    fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onGeolocationPermissionsShowPrompt(origin, callback)) {
                return true
            }
        }
        return false
    }

    fun onGeolocationPermissionsHidePrompt(): Boolean {
        abilities.forEach { ability ->
            if (ability.onGeolocationPermissionsHidePrompt()) {
                return true
            }
        }
        return false
    }

    fun onPermissionRequest(request: PermissionRequest?): Boolean {
        abilities.forEach { ability ->
            if (ability.onPermissionRequest(request)) {
                return true
            }
        }
        return false
    }

    fun onPermissionRequestCanceled(request: PermissionRequest?): Boolean {
        abilities.forEach { ability ->
            if (ability.onPermissionRequestCanceled(request)) {
                return true
            }
        }
        return false
    }

    fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        abilities.forEach { ability ->
            if (ability.onConsoleMessage(consoleMessage)) {
                return true
            }
        }
        return false
    }

    fun onShowFileChooser(
        webView: View,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        abilities.forEach { ability ->
            if (ability.onShowFileChooser(webView, filePathCallback, fileChooserParams)) {
                return true
            }
        }
        return false
    }

    fun onReceivedTouchIconUrl(webView: View, url: String?, precomposed: Boolean): Boolean {
        abilities.forEach { ability ->
            if (ability.onReceivedTouchIconUrl(webView, url, precomposed)) {
                return true
            }
        }
        return false
    }

    fun onReceivedIcon(webView: View, icon: Bitmap?): Boolean {
        abilities.forEach { ability ->
            if (ability.onReceivedIcon(webView, icon)) {
                return true
            }
        }
        return false
    }

    fun onReceivedTitle(webView: View, title: String?): Boolean {
        abilities.forEach { ability ->
            if (ability.onReceivedTitle(webView, title)) {
                return true
            }
        }
        return false
    }

    fun onProgressChanged(webView: View, newProgress: Int): Boolean {
        abilities.forEach { ability ->
            if (ability.onProgressChanged(webView, newProgress)) {
                return true
            }
        }
        return false
    }

    fun getVisitedHistory(callback: ValueCallback<Array<String>>?): Boolean {
        abilities.forEach { ability ->
            if (ability.getVisitedHistory(callback)) {
                return true
            }
        }
        return false
    }

    fun getVideoLoadingProgressView(): View? {
        abilities.forEach { ability ->
            ability.getVideoLoadingProgressView()?.let { view ->
                return view
            }
        }
        return null
    }

    fun getDefaultVideoPoster(): Bitmap? {
        abilities.forEach { ability ->
            ability.getDefaultVideoPoster()?.let { bitmap ->
                return bitmap
            }
        }
        return null
    }

    override fun onDownloadStart(
        url: String?,
        userAgent: String?,
        contentDisposition: String?,
        mimetype: String?,
        contentLength: Long
    ) {
        abilities.forEach { ability ->
            if (ability.onDownloadStart(
                    url,
                    userAgent,
                    contentDisposition,
                    mimetype,
                    contentLength
                )
            ) {
                return
            }
        }
    }
}