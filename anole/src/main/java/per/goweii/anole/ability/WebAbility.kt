package per.goweii.anole.ability

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Message
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import per.goweii.anole.kernel.WebKernel

open class WebAbility {

    open fun onAttachToKernel(kernel: WebKernel) {}

    open fun onDetachFromKernel(kernel: WebKernel) {}

    open fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean = false

    open fun onShowCustomView(
        customView: View?,
        callback: WebChromeClient.CustomViewCallback?
    ): Boolean = false

    open fun onHideCustomView(): Boolean = false

    open fun onShowFileChooser(
        webView: View,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean = false

    open fun getVisitedHistory(callback: ValueCallback<Array<String>>?): Boolean = false

    open fun doUpdateVisitedHistory(webView: View, url: String, isReload: Boolean): Boolean =
        false

    open fun shouldInterceptRequest(
        webView: View,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): WebResourceResponse? = null

    open fun onJsAlert(
        webView: View,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean = false

    open fun onJsPrompt(
        webView: View,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean = false

    open fun onJsConfirm(
        webView: View,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean = false

    open fun onJsBeforeUnload(
        webView: View,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean = false

    open fun onJsTimeout(): Boolean = false

    open fun shouldOverrideKeyEvent(webView: View, event: KeyEvent): Boolean = false

    open fun onUnhandledKeyEvent(webView: View, event: KeyEvent?): Boolean = false

    open fun onLoadResource(webView: View, url: String?): Boolean = false

    open fun shouldOverrideUrlLoading(
        webView: View,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): Boolean = false

    open fun onPageStarted(webView: View, url: String?, favicon: Bitmap?): Boolean = false

    open fun onPageFinished(webView: View, url: String?): Boolean = false

    open fun onProgressChanged(webView: View, newProgress: Int): Boolean = false

    open fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ): Boolean = false

    open fun onGeolocationPermissionsHidePrompt(): Boolean = false

    open fun onPermissionRequest(request: PermissionRequest?): Boolean = false

    open fun onPermissionRequestCanceled(request: PermissionRequest?): Boolean = false

    open fun onReceivedError(
        webView: View,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ): Boolean = false

    open fun onReceivedError(
        webView: View,
        request: WebResourceRequest?,
        error: WebResourceError?
    ): Boolean = false

    open fun onReceivedHttpError(
        webView: View,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ): Boolean = false

    open fun onRenderProcessGone(webView: View, detail: RenderProcessGoneDetail?): Boolean =
        false

    open fun onReceivedSslError(
        webView: View,
        handler: SslErrorHandler?,
        error: SslError?
    ): Boolean = false

    open fun onReceivedTouchIconUrl(
        webView: View,
        url: String?,
        precomposed: Boolean
    ): Boolean =
        false

    open fun onReceivedIcon(webView: View, icon: Bitmap?): Boolean = false

    open fun onReceivedTitle(webView: View, title: String?): Boolean = false

    open fun onReceivedClientCertRequest(webView: View, request: ClientCertRequest?): Boolean =
        false

    open fun onReceivedLoginRequest(
        webView: View,
        realm: String?,
        account: String?,
        args: String?
    ): Boolean = false

    open fun onReceivedHttpAuthRequest(
        webView: View,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ): Boolean = false

    open fun onRequestFocus(kernel: WebKernel): Boolean = false

    open fun onSafeBrowsingHit(
        webView: View,
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
        webView: View,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean = false

    open fun onCloseWindow(webView: View): Boolean = false

    open fun onDownloadStart(
        url: String?,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?,
        contentLength: Long
    ): Boolean = false
}
