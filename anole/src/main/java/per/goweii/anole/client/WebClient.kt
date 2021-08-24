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

    internal fun shouldInterceptRequest(
        webView: View,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): WebResourceResponse? {
        abilities.forEach { ability ->
            ability.shouldInterceptRequest(
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
        abilities.forEach { ability ->
            if (ability.shouldOverrideUrlLoading(
                    webView,
                    reqUri,
                    reqHeaders,
                    reqMethod,
                    userAgent
                )
            ) {
                return true
            }
        }
        return false
    }

    internal fun shouldOverrideKeyEvent(webView: View, event: KeyEvent): Boolean {
        abilities.forEach { ability ->
            if (ability.shouldOverrideKeyEvent(webView, event)) {
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
        abilities.forEach { ability ->
            if (ability.onSafeBrowsingHit(webView, request, threatType, callback)) {
                return
            }
        }
    }

    internal fun doUpdateVisitedHistory(webView: View, url: String, isReload: Boolean) {
        abilities.forEach { ability ->
            if (ability.doUpdateVisitedHistory(webView, url, isReload)) {
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
        abilities.forEach { ability ->
            if (ability.onReceivedError(webView, errorCode, description, failingUrl)) {
                return
            }
        }
    }

    internal fun onReceivedError(
        webView: View,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        abilities.forEach { ability ->
            if (ability.onReceivedError(webView, request, error)) {
                return
            }
        }
    }

    internal fun onReceivedHttpError(
        webView: View,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        abilities.forEach { ability ->
            if (ability.onReceivedHttpError(webView, request, errorResponse)) {
                return
            }
        }
    }

    internal fun onReceivedLoginRequest(
        webView: View,
        realm: String?,
        account: String?,
        args: String?
    ) {
        abilities.forEach { ability ->
            if (ability.onReceivedLoginRequest(webView, realm, account, args)) {
                return
            }
        }
    }

    internal fun onPageStarted(webView: View, url: String?, favicon: Bitmap?) {
        abilities.forEach { ability ->
            if (ability.onPageStarted(webView, url, favicon)) {
                return
            }
        }
    }

    internal fun onPageFinished(webView: View, url: String?) {
        abilities.forEach { ability ->
            if (ability.onPageFinished(webView, url)) {
                return
            }
        }
    }

    internal fun onScaleChanged(webView: View, oldScale: Float, newScale: Float) {
    }

    internal fun onPageCommitVisible(webView: View, url: String?) {
    }

    internal fun onUnhandledKeyEvent(webView: View, event: KeyEvent?) {
        abilities.forEach { ability ->
            if (ability.onUnhandledKeyEvent(webView, event)) {
                return
            }
        }
    }

    internal fun onReceivedClientCertRequest(webView: View, request: ClientCertRequest?) {
        abilities.forEach { ability ->
            if (ability.onReceivedClientCertRequest(webView, request)) {
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
        abilities.forEach { ability ->
            if (ability.onReceivedHttpAuthRequest(webView, handler, host, realm)) {
                return
            }
        }
    }

    internal fun onReceivedSslError(webView: View, handler: SslErrorHandler?, error: SslError?) {
        abilities.forEach { ability ->
            if (ability.onReceivedSslError(webView, handler, error)) {
                return
            }
        }
    }

    internal fun onTooManyRedirects(webView: View, cancelMsg: Message?, continueMsg: Message?) {
    }

    internal fun onFormResubmission(webView: View, dontResend: Message?, resend: Message?) {
    }

    internal fun onLoadResource(webView: View, url: String?) {
        abilities.forEach { ability ->
            if (ability.onLoadResource(webView, url)) {
                return
            }
        }
    }

    internal fun onRequestFocus(webView: View) {
        abilities.forEach { ability ->
            if (ability.onRequestFocus(kernel)) {
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
        abilities.forEach { ability ->
            if (ability.onJsAlert(webView, url, message, result)) {
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
        abilities.forEach { ability ->
            if (ability.onJsPrompt(webView, url, message, defaultValue, result)) {
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
        abilities.forEach { ability ->
            if (ability.onJsConfirm(webView, url, message, result)) {
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
        abilities.forEach { ability ->
            if (ability.onJsBeforeUnload(webView, url, message, result)) {
                return true
            }
        }
        return false
    }

    internal fun onJsTimeout(): Boolean {
        abilities.forEach { ability ->
            if (ability.onJsTimeout()) {
                return true
            }
        }
        return false
    }

    internal fun onShowCustomView(
        customView: View?,
        callback: CustomViewCallback?
    ) {
        abilities.forEach { ability ->
            if (ability.onShowCustomView(customView, callback)) {
                return
            }
        }
    }

    internal fun onHideCustomView() {
        abilities.forEach { ability ->
            if (ability.onHideCustomView()) {
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
        abilities.forEach { ability ->
            if (ability.onCreateWindow(webView, isDialog, isUserGesture, resultMsg)) {
                return true
            }
        }
        return false
    }

    internal fun onCloseWindow(webView: View) {
        abilities.forEach { ability ->
            if (ability.onCloseWindow(webView)) {
                return
            }
        }
    }

    internal fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ) {
        abilities.forEach { ability ->
            if (ability.onGeolocationPermissionsShowPrompt(origin, callback)) {
                return
            }
        }
    }

    internal fun onGeolocationPermissionsHidePrompt() {
        abilities.forEach { ability ->
            if (ability.onGeolocationPermissionsHidePrompt()) {
                return
            }
        }
    }

    internal fun onPermissionRequest(request: PermissionRequest?): Boolean {
        abilities.forEach { ability ->
            if (ability.onPermissionRequest(request)) {
                return true
            }
        }
        return false
    }

    internal fun onPermissionRequestCanceled(request: PermissionRequest?): Boolean {
        abilities.forEach { ability ->
            if (ability.onPermissionRequestCanceled(request)) {
                return true
            }
        }
        return false
    }

    internal fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        abilities.forEach { ability ->
            if (ability.onConsoleMessage(consoleMessage)) {
                return true
            }
        }
        return false
    }

    internal fun onShowFileChooser(
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

    internal fun onReceivedTouchIconUrl(webView: View, url: String?, precomposed: Boolean) {
        abilities.forEach { ability ->
            if (ability.onReceivedTouchIconUrl(webView, url, precomposed)) {
                return
            }
        }
    }

    internal fun onReceivedIcon(webView: View, icon: Bitmap?) {
        abilities.forEach { ability ->
            if (ability.onReceivedIcon(webView, icon)) {
                return
            }
        }
    }

    internal fun onReceivedTitle(webView: View, title: String?) {
        abilities.forEach { ability ->
            if (ability.onReceivedTitle(webView, title)) {
                return
            }
        }
    }

    internal fun onProgressChanged(webView: View, newProgress: Int) {
        abilities.forEach { ability ->
            if (ability.onProgressChanged(webView, newProgress)) {
                return
            }
        }
    }

    internal fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
        abilities.forEach { ability ->
            if (ability.getVisitedHistory(callback)) {
                return
            }
        }
    }

    internal fun getVideoLoadingProgressView(): View? {
        abilities.forEach { ability ->
            ability.getVideoLoadingProgressView()?.let { view ->
                return view
            }
        }
        return null
    }

    internal fun getDefaultVideoPoster(): Bitmap? {
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