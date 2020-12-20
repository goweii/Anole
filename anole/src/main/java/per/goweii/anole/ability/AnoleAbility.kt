package per.goweii.anole.ability

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import per.goweii.anole.view.AnoleView

open class AnoleAbility {

    open fun onAttachToWebView(anoleView: AnoleView) {}

    open fun onDetachFromWebView(anoleView: AnoleView) {}

    open fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean = false

    open fun onShowCustomView(
        view: View?,
        callback: WebChromeClient.CustomViewCallback?
    ): Boolean = false

    open fun onHideCustomView(): Boolean = false

    open fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean = false

    open fun getVisitedHistory(callback: ValueCallback<Array<String>>?): Boolean = false

    open fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean): Boolean = false

    open fun shouldInterceptRequest(
        view: WebView,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): WebResourceResponse? = null

    open fun onJsAlert(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean = false

    open fun onJsPrompt(
        view: WebView,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean = false

    open fun onJsConfirm(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean = false

    open fun onJsBeforeUnload(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean = false

    open fun onJsTimeout(): Boolean = false

    open fun shouldOverrideKeyEvent(view: WebView, event: KeyEvent): Boolean = false

    open fun onUnhandledKeyEvent(view: WebView, event: KeyEvent?): Boolean = false

    open fun onLoadResource(view: WebView, url: String?): Boolean = false

    open fun shouldOverrideUrlLoading(
        view: WebView,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): Boolean = false

    open fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?): Boolean = false

    open fun onPageFinished(view: WebView, url: String?): Boolean = false

    open fun onProgressChanged(view: WebView, newProgress: Int): Boolean = false

    open fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ): Boolean = false

    open fun onGeolocationPermissionsHidePrompt(): Boolean = false

    open fun onPermissionRequest(request: PermissionRequest?): Boolean = false

    open fun onPermissionRequestCanceled(request: PermissionRequest?): Boolean = false

    open fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ): Boolean = false

    open fun onReceivedError(
        view: WebView,
        request: WebResourceRequest?,
        error: WebResourceError?
    ): Boolean = false

    open fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ): Boolean = false

    open fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail?): Boolean =
        false

    open fun onReceivedSslError(
        view: WebView,
        handler: SslErrorHandler?,
        error: SslError?
    ): Boolean = false

    open fun onReceivedTouchIconUrl(view: WebView, url: String?, precomposed: Boolean): Boolean =
        false

    open fun onReceivedIcon(view: WebView, icon: Bitmap?): Boolean = false

    open fun onReceivedTitle(view: WebView, title: String?): Boolean = false

    open fun onReceivedClientCertRequest(view: WebView, request: ClientCertRequest?): Boolean =
        false

    open fun onReceivedLoginRequest(
        view: WebView,
        realm: String?,
        account: String?,
        args: String?
    ): Boolean = false

    open fun onReceivedHttpAuthRequest(
        view: WebView,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ): Boolean = false

    open fun onRequestFocus(view: WebView): Boolean = false

    open fun onSafeBrowsingHit(
        view: WebView,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ): Boolean = false

    open fun onExceededDatabaseQuota(
        url: String?,
        databaseIdentifier: String?,
        quota: Long,
        estimatedDatabaseSize: Long,
        totalQuota: Long,
        quotaUpdater: WebStorage.QuotaUpdater?
    ): Boolean = false

    open fun onReachedMaxAppCacheSize(
        requiredStorage: Long,
        quota: Long,
        quotaUpdater: WebStorage.QuotaUpdater?
    ): Boolean = false

    open fun getVideoLoadingProgressView(): View? = null

    open fun getDefaultVideoPoster(): Bitmap? = null

    open fun onCreateWindow(
        view: WebView,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean = false

    open fun onCloseWindow(window: WebView): Boolean = false

    open fun onDownloadStart(
        url: String?,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?,
        contentLength: Long
    ): Boolean = false
}
