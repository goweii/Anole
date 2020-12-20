package per.goweii.anole.client

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.webkit.WebChromeClient
import per.goweii.anole.ability.AnoleAbility
import per.goweii.anole.view.AnoleView

class AnoleClient(private val anoleView: AnoleView) : DownloadListener {
    private val abilities = arrayListOf<AnoleAbility>()

    fun containsAbility(client: AnoleAbility): Boolean {
        return abilities.contains(client)
    }

    fun addAbility(client: AnoleAbility) {
        abilities.add(client)
        client.onAttachToWebView(anoleView)
    }

    fun removeAbility(client: AnoleAbility) {
        client.onDetachFromWebView(anoleView)
        abilities.remove(client)
    }

    fun clearAbilities() {
        abilities.forEach { it.onDetachFromWebView(anoleView) }
        abilities.clear()
    }

    internal fun shouldInterceptRequest(
        view: WebView,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): WebResourceResponse? {
        abilities.forEach { client ->
            client.shouldInterceptRequest(
                view, reqUri, reqHeaders, reqMethod, userAgent
            )?.let { resp ->
                return resp
            }
        }
        return null
    }

    internal fun shouldOverrideUrlLoading(
        view: WebView,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): Boolean {
        abilities.forEach { client ->
            if (client.shouldOverrideUrlLoading(view, reqUri, reqHeaders, reqMethod, userAgent)) {
                return true
            }
        }
        return false
    }

    internal fun shouldOverrideKeyEvent(view: WebView, event: KeyEvent): Boolean {
        abilities.forEach { client ->
            if (client.shouldOverrideKeyEvent(view, event)) {
                return true
            }
        }
        return false
    }

    internal fun onSafeBrowsingHit(
        view: WebView,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ) {
        abilities.forEach { client ->
            if (client.onSafeBrowsingHit(view, request, threatType, callback)) {
                return
            }
        }
    }

    internal fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
        abilities.forEach { client ->
            if (client.doUpdateVisitedHistory(view, url, isReload)) {
                return
            }
        }
    }

    internal fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        abilities.forEach { client ->
            if (client.onReceivedError(view, errorCode, description, failingUrl)) {
                return
            }
        }
    }

    internal fun onReceivedError(
        view: WebView,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        abilities.forEach { client ->
            if (client.onReceivedError(view, request, error)) {
                return
            }
        }
    }

    internal fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        abilities.forEach { client ->
            if (client.onReceivedHttpError(view, request, errorResponse)) {
                return
            }
        }
    }

    internal fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail?): Boolean {
        abilities.forEach { client ->
            if (client.onRenderProcessGone(view, detail)) {
                return true
            }
        }
        return false
    }

    internal fun onReceivedLoginRequest(
        view: WebView,
        realm: String?,
        account: String?,
        args: String?
    ) {
        abilities.forEach { client ->
            if (client.onReceivedLoginRequest(view, realm, account, args)) {
                return
            }
        }
    }

    internal fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        abilities.forEach { client ->
            if (client.onPageStarted(view, url, favicon)) {
                return
            }
        }
    }

    internal fun onPageFinished(view: WebView, url: String?) {
        abilities.forEach { client ->
            if (client.onPageFinished(view, url)) {
                return
            }
        }
    }

    internal fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
    }

    internal fun onPageCommitVisible(view: WebView, url: String?) {
    }

    internal fun onUnhandledKeyEvent(view: WebView, event: KeyEvent?) {
        abilities.forEach { client ->
            if (client.onUnhandledKeyEvent(view, event)) {
                return
            }
        }
    }

    internal fun onReceivedClientCertRequest(view: WebView, request: ClientCertRequest?) {
        abilities.forEach { client ->
            if (client.onReceivedClientCertRequest(view, request)) {
                return
            }
        }
    }

    internal fun onReceivedHttpAuthRequest(
        view: WebView,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        abilities.forEach { client ->
            if (client.onReceivedHttpAuthRequest(view, handler, host, realm)) {
                return
            }
        }
    }

    internal fun onReceivedSslError(view: WebView, handler: SslErrorHandler?, error: SslError?) {
        abilities.forEach { client ->
            if (client.onReceivedSslError(view, handler, error)) {
                return
            }
        }
    }

    internal fun onTooManyRedirects(view: WebView, cancelMsg: Message?, continueMsg: Message?) {
    }

    internal fun onFormResubmission(view: WebView, dontResend: Message?, resend: Message?) {
    }

    internal fun onLoadResource(view: WebView, url: String?) {
        abilities.forEach { client ->
            if (client.onLoadResource(view, url)) {
                return
            }
        }
    }

    internal fun onRequestFocus(view: WebView) {
        abilities.forEach { client ->
            if (client.onRequestFocus(view)) {
                return
            }
        }
    }

    internal fun onJsAlert(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        abilities.forEach { client ->
            if (client.onJsAlert(view, url, message, result)) {
                return true
            }
        }
        return false
    }

    internal fun onJsPrompt(
        view: WebView,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        abilities.forEach { client ->
            if (client.onJsPrompt(view, url, message, defaultValue, result)) {
                return true
            }
        }
        return false
    }

    internal fun onJsConfirm(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        abilities.forEach { client ->
            if (client.onJsConfirm(view, url, message, result)) {
                return true
            }
        }
        return false
    }

    internal fun onJsBeforeUnload(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        abilities.forEach { client ->
            if (client.onJsBeforeUnload(view, url, message, result)) {
                return true
            }
        }
        return false
    }

    internal fun onJsTimeout(): Boolean {
        abilities.forEach { client ->
            if (client.onJsTimeout()) {
                return true
            }
        }
        return false
    }

    internal fun onShowCustomView(view: View?, callback: WebChromeClient.CustomViewCallback?) {
        abilities.forEach { client ->
            if (client.onShowCustomView(view, callback)) {
                return
            }
        }
    }

    internal fun onHideCustomView() {
        abilities.forEach { client ->
            if (client.onHideCustomView()) {
                return
            }
        }
    }

    internal fun onCreateWindow(
        view: WebView,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean {
        abilities.forEach { client ->
            if (client.onCreateWindow(view, isDialog, isUserGesture, resultMsg)) {
                return true
            }
        }
        return false
    }

    internal fun onCloseWindow(window: WebView) {
        abilities.forEach { client ->
            if (client.onCloseWindow(window)) {
                return
            }
        }
    }

    internal fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ) {
        abilities.forEach { client ->
            if (client.onGeolocationPermissionsShowPrompt(origin, callback)) {
                return
            }
        }
    }

    internal fun onGeolocationPermissionsHidePrompt() {
        abilities.forEach { client ->
            if (client.onGeolocationPermissionsHidePrompt()) {
                return
            }
        }
    }

    internal fun onPermissionRequest(request: PermissionRequest?): Boolean {
        abilities.forEach { client ->
            if (client.onPermissionRequest(request)) {
                return true
            }
        }
        return false
    }

    internal fun onPermissionRequestCanceled(request: PermissionRequest?): Boolean {
        abilities.forEach { client ->
            if (client.onPermissionRequestCanceled(request)) {
                return true
            }
        }
        return false
    }

    internal fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        abilities.forEach { client ->
            if (client.onConsoleMessage(consoleMessage)) {
                return true
            }
        }
        return false
    }

    internal fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        abilities.forEach { client ->
            if (client.onShowFileChooser(webView, filePathCallback, fileChooserParams)) {
                return true
            }
        }
        return false
    }

    internal fun onReceivedTouchIconUrl(view: WebView, url: String?, precomposed: Boolean) {
        abilities.forEach { client ->
            if (client.onReceivedTouchIconUrl(view, url, precomposed)) {
                return
            }
        }
    }

    internal fun onReceivedIcon(view: WebView, icon: Bitmap?) {
        abilities.forEach { client ->
            if (client.onReceivedIcon(view, icon)) {
                return
            }
        }
    }

    internal fun onReceivedTitle(view: WebView, title: String?) {
        abilities.forEach { client ->
            if (client.onReceivedTitle(view, title)) {
                return
            }
        }
    }

    internal fun onProgressChanged(view: WebView, newProgress: Int) {
        abilities.forEach { client ->
            if (client.onProgressChanged(view, newProgress)) {
                return
            }
        }
    }

    internal fun onExceededDatabaseQuota(
        url: String?,
        databaseIdentifier: String?,
        quota: Long,
        estimatedDatabaseSize: Long,
        totalQuota: Long,
        quotaUpdater: WebStorage.QuotaUpdater?
    ) {
        abilities.forEach { client ->
            if (client.onExceededDatabaseQuota(
                    url,
                    databaseIdentifier,
                    quota,
                    estimatedDatabaseSize,
                    totalQuota,
                    quotaUpdater
                )
            ) {
                return
            }
        }
    }

    internal fun onReachedMaxAppCacheSize(
        requiredStorage: Long,
        quota: Long,
        quotaUpdater: WebStorage.QuotaUpdater?
    ) {
        abilities.forEach { client ->
            if (client.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)) {
                return
            }
        }
    }

    internal fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
        abilities.forEach { client ->
            if (client.getVisitedHistory(callback)) {
                return
            }
        }
    }

    internal fun getVideoLoadingProgressView(): View? {
        abilities.forEach { client ->
            client.getVideoLoadingProgressView()?.let { view ->
                return view
            }
        }
        return null
    }

    internal fun getDefaultVideoPoster(): Bitmap? {
        abilities.forEach { client ->
            client.getDefaultVideoPoster()?.let { bitmap ->
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
        abilities.forEach { client ->
            if (client.onDownloadStart(
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