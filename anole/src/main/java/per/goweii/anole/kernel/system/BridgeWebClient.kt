package per.goweii.anole.kernel.system

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import android.webkit.WebChromeClient
import per.goweii.anole.client.WebClient

class BridgeWebClient(
    private val webClient: WebClient
) {
    fun shouldInterceptRequest(
        view: WebView,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): WebResourceResponse? {
        return webClient.shouldInterceptRequest(
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
        return webClient.shouldOverrideUrlLoading(
            view,
            reqUri,
            reqHeaders,
            reqMethod,
            userAgent
        )
    }

    fun shouldOverrideKeyEvent(view: WebView, event: KeyEvent): Boolean {
        return webClient.shouldOverrideKeyEvent(view, event)
    }

    fun onSafeBrowsingHit(
        view: WebView,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ) {
        webClient.onSafeBrowsingHit(view, request, threatType, callback)
    }

    fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
        webClient.doUpdateVisitedHistory(view, url, isReload)
    }

    fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        webClient.onReceivedError(view, errorCode, description, failingUrl)
    }

    fun onReceivedError(
        view: WebView,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        webClient.onReceivedError(view, request, error)
    }

    fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        webClient.onReceivedHttpError(view, request, errorResponse)
    }

    fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail?): Boolean {
        return webClient.onRenderProcessGone(view, detail)
    }

    fun onReceivedLoginRequest(
        view: WebView,
        realm: String?,
        account: String?,
        args: String?
    ) {
        webClient.onReceivedLoginRequest(view, realm, account, args)
    }

    fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        webClient.onPageStarted(view, url, favicon)
    }

    fun onPageFinished(view: WebView, url: String?) {
        webClient.onPageFinished(view, url)
    }

    fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
        webClient.onScaleChanged(view, oldScale, newScale)
    }

    fun onPageCommitVisible(view: WebView, url: String?) {
        webClient.onPageCommitVisible(view, url)
    }

    fun onUnhandledKeyEvent(view: WebView, event: KeyEvent?) {
        webClient.onUnhandledKeyEvent(view, event)
    }

    fun onReceivedClientCertRequest(view: WebView, request: ClientCertRequest?) {
        webClient.onReceivedClientCertRequest(view, request)
    }

    fun onReceivedHttpAuthRequest(
        view: WebView,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        webClient.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    fun onReceivedSslError(view: WebView, handler: SslErrorHandler?, error: SslError?) {
        webClient.onReceivedSslError(view, handler, error)
    }

    fun onTooManyRedirects(view: WebView, cancelMsg: Message?, continueMsg: Message?) {
        webClient.onTooManyRedirects(view, cancelMsg, continueMsg)
    }

    fun onFormResubmission(view: WebView, dontResend: Message?, resend: Message?) {
        webClient.onFormResubmission(view, dontResend, resend)
    }

    fun onLoadResource(view: WebView, url: String?) {
        webClient.onLoadResource(view, url)
    }

    fun onRequestFocus(view: WebView) {
        webClient.onRequestFocus(view)
    }

    fun onJsAlert(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return webClient.onJsAlert(view, url, message, result)
    }

    fun onJsPrompt(
        view: WebView,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        return webClient.onJsPrompt(view, url, message, defaultValue, result)
    }

    fun onJsConfirm(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return webClient.onJsConfirm(view, url, message, result)
    }

    fun onJsBeforeUnload(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        return webClient.onJsBeforeUnload(view, url, message, result)
    }

    fun onJsTimeout(): Boolean {
        return webClient.onJsTimeout()
    }

    fun onShowCustomView(view: View?, callback: WebChromeClient.CustomViewCallback?) {
        webClient.onShowCustomView(view, callback)
    }

    fun onHideCustomView() {
        webClient.onHideCustomView()
    }

    fun onCreateWindow(
        view: WebView,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean {
        return webClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    fun onCloseWindow(window: WebView) {
        webClient.onCloseWindow(window)
    }

    fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ) {
        webClient.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    fun onGeolocationPermissionsHidePrompt() {
        webClient.onGeolocationPermissionsHidePrompt()
    }

    fun onPermissionRequest(request: PermissionRequest?): Boolean {
        return webClient.onPermissionRequest(request)
    }

    fun onPermissionRequestCanceled(request: PermissionRequest?): Boolean {
        return webClient.onPermissionRequestCanceled(request)
    }

    fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        return webClient.onConsoleMessage(consoleMessage)
    }

    fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        return webClient.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    fun onReceivedTouchIconUrl(view: WebView, url: String?, precomposed: Boolean) {
        webClient.onReceivedTouchIconUrl(view, url, precomposed)
    }

    fun onReceivedIcon(view: WebView, icon: Bitmap?) {
        webClient.onReceivedIcon(view, icon)
    }

    fun onReceivedTitle(view: WebView, title: String?) {
        webClient.onReceivedTitle(view, title)
    }

    fun onProgressChanged(view: WebView, newProgress: Int) {
        webClient.onProgressChanged(view, newProgress)
    }

    fun onExceededDatabaseQuota(
        url: String?,
        databaseIdentifier: String?,
        quota: Long,
        estimatedDatabaseSize: Long,
        totalQuota: Long,
        quotaUpdater: WebStorage.QuotaUpdater?
    ) {
        webClient.onExceededDatabaseQuota(
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
        webClient.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
    }

    fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
        webClient.getVisitedHistory(callback)
    }

    fun getVideoLoadingProgressView(): View? {
        return webClient.getVideoLoadingProgressView()
    }

    fun getDefaultVideoPoster(): Bitmap? {
        return webClient.getDefaultVideoPoster()
    }
}