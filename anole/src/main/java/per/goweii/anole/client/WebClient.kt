package per.goweii.anole.client

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.DownloadListener
import per.goweii.anole.kernel.WebKernel

class WebClient(private val kernel: WebKernel) : DownloadListener {
    private val abilities = arrayListOf<WebAbility>()

    fun containsAbility(client: WebAbility): Boolean {
        return abilities.contains(client)
    }

    fun addAbility(client: WebAbility) {
        abilities.add(client)
        client.onAttachToKernel(kernel)
    }

    fun removeAbility(client: WebAbility) {
        client.onDetachFromKernel(kernel)
        abilities.remove(client)
    }

    fun clearAbilities() {
        abilities.forEach { it.onDetachFromKernel(kernel) }
        abilities.clear()
    }

    internal fun shouldInterceptRequest(
        webView: View,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): WebResourceResponse? {
        abilities.forEach { client ->
            client.shouldInterceptRequest(
                webView, reqUri, reqHeaders, reqMethod, userAgent
            )?.let { resp ->
                return resp
            }
        }
        return null
    }

    internal fun shouldOverrideUrlLoading(
        webView: View,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): Boolean {
        abilities.forEach { client ->
            if (client.shouldOverrideUrlLoading(webView, reqUri, reqHeaders, reqMethod, userAgent)) {
                return true
            }
        }
        return false
    }

    internal fun shouldOverrideKeyEvent(webView: View, event: KeyEvent): Boolean {
        abilities.forEach { client ->
            if (client.shouldOverrideKeyEvent(webView, event)) {
                return true
            }
        }
        return false
    }

    internal fun onSafeBrowsingHit(
        webView: View,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ) {
        abilities.forEach { client ->
            if (client.onSafeBrowsingHit(webView, request, threatType, callback)) {
                return
            }
        }
    }

    internal fun doUpdateVisitedHistory(webView: View, url: String, isReload: Boolean) {
        abilities.forEach { client ->
            if (client.doUpdateVisitedHistory(webView, url, isReload)) {
                return
            }
        }
    }

    internal fun onReceivedError(
        webView: View,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        abilities.forEach { client ->
            if (client.onReceivedError(webView, errorCode, description, failingUrl)) {
                return
            }
        }
    }

    internal fun onReceivedError(
        webView: View,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        abilities.forEach { client ->
            if (client.onReceivedError(webView, request, error)) {
                return
            }
        }
    }

    internal fun onReceivedHttpError(
        webView: View,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        abilities.forEach { client ->
            if (client.onReceivedHttpError(webView, request, errorResponse)) {
                return
            }
        }
    }

    internal fun onRenderProcessGone(webView: View, detail: RenderProcessGoneDetail?): Boolean {
        abilities.forEach { client ->
            if (client.onRenderProcessGone(webView, detail)) {
                return true
            }
        }
        return false
    }

    internal fun onReceivedLoginRequest(
        webView: View,
        realm: String?,
        account: String?,
        args: String?
    ) {
        abilities.forEach { client ->
            if (client.onReceivedLoginRequest(webView, realm, account, args)) {
                return
            }
        }
    }

    internal fun onPageStarted(webView: View, url: String?, favicon: Bitmap?) {
        abilities.forEach { client ->
            if (client.onPageStarted(webView, url, favicon)) {
                return
            }
        }
    }

    internal fun onPageFinished(webView: View, url: String?) {
        abilities.forEach { client ->
            if (client.onPageFinished(webView, url)) {
                return
            }
        }
    }

    internal fun onScaleChanged(webView: View, oldScale: Float, newScale: Float) {
    }

    internal fun onPageCommitVisible(webView: View, url: String?) {
    }

    internal fun onUnhandledKeyEvent(webView: View, event: KeyEvent?) {
        abilities.forEach { client ->
            if (client.onUnhandledKeyEvent(webView, event)) {
                return
            }
        }
    }

    internal fun onReceivedClientCertRequest(webView: View, request: ClientCertRequest?) {
        abilities.forEach { client ->
            if (client.onReceivedClientCertRequest(webView, request)) {
                return
            }
        }
    }

    internal fun onReceivedHttpAuthRequest(
        webView: View,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        abilities.forEach { client ->
            if (client.onReceivedHttpAuthRequest(webView, handler, host, realm)) {
                return
            }
        }
    }

    internal fun onReceivedSslError(webView: View, handler: SslErrorHandler?, error: SslError?) {
        abilities.forEach { client ->
            if (client.onReceivedSslError(webView, handler, error)) {
                return
            }
        }
    }

    internal fun onTooManyRedirects(webView: View, cancelMsg: Message?, continueMsg: Message?) {
    }

    internal fun onFormResubmission(webView: View, dontResend: Message?, resend: Message?) {
    }

    internal fun onLoadResource(webView: View, url: String?) {
        abilities.forEach { client ->
            if (client.onLoadResource(webView, url)) {
                return
            }
        }
    }

    internal fun onRequestFocus(webView: View) {
        abilities.forEach { client ->
            if (client.onRequestFocus(kernel)) {
                return
            }
        }
    }

    internal fun onJsAlert(
        webView: View,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        abilities.forEach { client ->
            if (client.onJsAlert(webView, url, message, result)) {
                return true
            }
        }
        return false
    }

    internal fun onJsPrompt(
        webView: View,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        abilities.forEach { client ->
            if (client.onJsPrompt(webView, url, message, defaultValue, result)) {
                return true
            }
        }
        return false
    }

    internal fun onJsConfirm(
        webView: View,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        abilities.forEach { client ->
            if (client.onJsConfirm(webView, url, message, result)) {
                return true
            }
        }
        return false
    }

    internal fun onJsBeforeUnload(
        webView: View,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        abilities.forEach { client ->
            if (client.onJsBeforeUnload(webView, url, message, result)) {
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

    internal fun onShowCustomView(
        customView: View?,
        callback: WebChromeClient.CustomViewCallback?
    ) {
        abilities.forEach { client ->
            if (client.onShowCustomView(customView, callback)) {
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
        webView: View,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean {
        abilities.forEach { client ->
            if (client.onCreateWindow(webView, isDialog, isUserGesture, resultMsg)) {
                return true
            }
        }
        return false
    }

    internal fun onCloseWindow(webView: View) {
        abilities.forEach { client ->
            if (client.onCloseWindow(webView)) {
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
        webView: View,
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

    internal fun onReceivedTouchIconUrl(webView: View, url: String?, precomposed: Boolean) {
        abilities.forEach { client ->
            if (client.onReceivedTouchIconUrl(webView, url, precomposed)) {
                return
            }
        }
    }

    internal fun onReceivedIcon(webView: View, icon: Bitmap?) {
        abilities.forEach { client ->
            if (client.onReceivedIcon(webView, icon)) {
                return
            }
        }
    }

    internal fun onReceivedTitle(webView: View, title: String?) {
        abilities.forEach { client ->
            if (client.onReceivedTitle(webView, title)) {
                return
            }
        }
    }

    internal fun onProgressChanged(webView: View, newProgress: Int) {
        abilities.forEach { client ->
            if (client.onProgressChanged(webView, newProgress)) {
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