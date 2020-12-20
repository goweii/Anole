package per.goweii.anole.client

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.webkit.WebChromeClient

/**
 * 对BridgeWebClient内回调方法进行版本兼容合并
 */
class MixedWebClient(
    private val anoleClient: AnoleClient
) {
    fun shouldInterceptRequest(
        view: WebView,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): WebResourceResponse? {
        return anoleClient.shouldInterceptRequest(
            view,
            reqUri,
            reqHeaders,
            reqMethod,
            userAgent
        )
    }

    fun shouldOverrideUrlLoading(
        view: WebView,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): Boolean {
        return anoleClient.shouldOverrideUrlLoading(
            view,
            reqUri,
            reqHeaders,
            reqMethod,
            userAgent
        )
    }

    fun shouldOverrideKeyEvent(view: WebView, event: KeyEvent): Boolean {
        return anoleClient.shouldOverrideKeyEvent(view, event)
    }

    fun onSafeBrowsingHit(
        view: WebView,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ) {
        anoleClient.onSafeBrowsingHit(view, request, threatType, callback)
    }

    fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
        anoleClient.doUpdateVisitedHistory(view, url, isReload)
    }

    fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        anoleClient.onReceivedError(view, errorCode, description, failingUrl)
    }

    fun onReceivedError(
        view: WebView,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        anoleClient.onReceivedError(view, request, error)
    }

    fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        anoleClient.onReceivedHttpError(view, request, errorResponse)
    }

    fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail?): Boolean {
        return anoleClient.onRenderProcessGone(view, detail)
    }

    fun onReceivedLoginRequest(
        view: WebView,
        realm: String?,
        account: String?,
        args: String?
    ) {
        anoleClient.onReceivedLoginRequest(view, realm, account, args)
    }

    fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        anoleClient.onPageStarted(view, url, favicon)
    }

    fun onPageFinished(view: WebView, url: String?) {
        anoleClient.onPageFinished(view, url)
    }

    fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
        anoleClient.onScaleChanged(view, oldScale, newScale)
    }

    fun onPageCommitVisible(view: WebView, url: String?) {
        anoleClient.onPageCommitVisible(view, url)
    }

    fun onUnhandledKeyEvent(view: WebView, event: KeyEvent?) {
        anoleClient.onUnhandledKeyEvent(view, event)
    }

    fun onReceivedClientCertRequest(view: WebView, request: ClientCertRequest?) {
        anoleClient.onReceivedClientCertRequest(view, request)
    }

    fun onReceivedHttpAuthRequest(
        view: WebView,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        anoleClient.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    fun onReceivedSslError(view: WebView, handler: SslErrorHandler?, error: SslError?) {
        anoleClient.onReceivedSslError(view, handler, error)
    }

    fun onTooManyRedirects(view: WebView, cancelMsg: Message?, continueMsg: Message?) {
        anoleClient.onTooManyRedirects(view, cancelMsg, continueMsg)
    }

    fun onFormResubmission(view: WebView, dontResend: Message?, resend: Message?) {
        anoleClient.onFormResubmission(view, dontResend, resend)
    }

    fun onLoadResource(view: WebView, url: String?) {
        anoleClient.onLoadResource(view, url)
    }

    fun onRequestFocus(view: WebView) {
        anoleClient.onRequestFocus(view)
    }

    fun onJsAlert(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return anoleClient.onJsAlert(view, url, message, result)
    }

    fun onJsPrompt(
        view: WebView,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        return anoleClient.onJsPrompt(view, url, message, defaultValue, result)
    }

    fun onJsConfirm(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return anoleClient.onJsConfirm(view, url, message, result)
    }

    fun onJsBeforeUnload(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return anoleClient.onJsBeforeUnload(view, url, message, result)
    }

    fun onJsTimeout(): Boolean {
        return anoleClient.onJsTimeout()
    }

    fun onShowCustomView(view: View?, callback: WebChromeClient.CustomViewCallback?) {
        anoleClient.onShowCustomView(view, callback)
    }

    fun onHideCustomView() {
        anoleClient.onHideCustomView()
    }

    fun onCreateWindow(
        view: WebView,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean {
        return anoleClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    fun onCloseWindow(window: WebView) {
        anoleClient.onCloseWindow(window)
    }

    fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ) {
        anoleClient.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    fun onGeolocationPermissionsHidePrompt() {
        anoleClient.onGeolocationPermissionsHidePrompt()
    }

    fun onPermissionRequest(request: PermissionRequest?): Boolean {
        return anoleClient.onPermissionRequest(request)
    }

    fun onPermissionRequestCanceled(request: PermissionRequest?): Boolean {
        return anoleClient.onPermissionRequestCanceled(request)
    }

    fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        return anoleClient.onConsoleMessage(consoleMessage)
    }

    fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        return anoleClient.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    fun onReceivedTouchIconUrl(view: WebView, url: String?, precomposed: Boolean) {
        anoleClient.onReceivedTouchIconUrl(view, url, precomposed)
    }

    fun onReceivedIcon(view: WebView, icon: Bitmap?) {
        anoleClient.onReceivedIcon(view, icon)
    }

    fun onReceivedTitle(view: WebView, title: String?) {
        anoleClient.onReceivedTitle(view, title)
    }

    fun onProgressChanged(view: WebView, newProgress: Int) {
        anoleClient.onProgressChanged(view, newProgress)
    }

    fun onExceededDatabaseQuota(
        url: String?,
        databaseIdentifier: String?,
        quota: Long,
        estimatedDatabaseSize: Long,
        totalQuota: Long,
        quotaUpdater: WebStorage.QuotaUpdater?
    ) {
        anoleClient.onExceededDatabaseQuota(
            url,
            databaseIdentifier,
            quota,
            estimatedDatabaseSize,
            totalQuota,
            quotaUpdater
        )
    }

    fun onReachedMaxAppCacheSize(
        requiredStorage: Long,
        quota: Long,
        quotaUpdater: WebStorage.QuotaUpdater?
    ) {
        anoleClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
    }

    fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
        anoleClient.getVisitedHistory(callback)
    }

    fun getVideoLoadingProgressView(): View? {
        return anoleClient.getVideoLoadingProgressView()
    }

    fun getDefaultVideoPoster(): Bitmap? {
        return anoleClient.getDefaultVideoPoster()
    }
}