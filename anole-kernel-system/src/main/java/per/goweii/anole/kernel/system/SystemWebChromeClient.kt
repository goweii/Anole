package per.goweii.anole.kernel.system

import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.view.View
import android.webkit.*
import per.goweii.anole.client.WebClient

class SystemWebChromeClient(
    private val webClient: WebClient
) : WebChromeClient() {

    override fun onRequestFocus(view: WebView) {
        if (!webClient.onRequestFocus(view)) {
            super.onRequestFocus(view)
        }
    }

    override fun onJsAlert(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        if (webClient.onJsAlert(view, url, message, result?.toLibraryJsResult())) {
            return true
        }
        return super.onJsAlert(view, url, message, result)
    }

    override fun onJsPrompt(
        view: WebView,
        url: String?,
        message: String?,
        defaultValue: String?,
        result: JsPromptResult?
    ): Boolean {
        if (webClient.onJsPrompt(
                view,
                url,
                message,
                defaultValue,
                result?.toLibraryJsPromptResult()
            )
        ) {
            return true
        }
        return super.onJsPrompt(view, url, message, defaultValue, result)
    }

    override fun onJsConfirm(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        if (webClient.onJsConfirm(view, url, message, result?.toLibraryJsResult())) {
            return true
        }
        return super.onJsConfirm(view, url, message, result)
    }

    override fun onJsBeforeUnload(
        view: WebView,
        url: String?,
        message: String?,
        result: JsResult?
    ): Boolean {
        if (webClient.onJsBeforeUnload(view, url, message, result?.toLibraryJsResult())) {
            return true
        }
        return super.onJsBeforeUnload(view, url, message, result)
    }

    override fun onJsTimeout(): Boolean {
        if (webClient.onJsTimeout()) {
            return true
        }
        return super.onJsTimeout()
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        if (!webClient.onShowCustomView(view, callback?.toLibraryCustomViewCallback())) {
            super.onShowCustomView(view, callback)
        }
    }

    override fun onShowCustomView(
        view: View?,
        requestedOrientation: Int,
        callback: CustomViewCallback?
    ) {
        if (!webClient.onShowCustomView(view, callback?.toLibraryCustomViewCallback())) {
            super.onShowCustomView(view, requestedOrientation, callback)
        }
    }

    override fun onHideCustomView() {
        if (!webClient.onHideCustomView()) {
            super.onHideCustomView()
        }
    }

    override fun onCreateWindow(
        view: WebView,
        isDialog: Boolean,
        isUserGesture: Boolean,
        resultMsg: Message
    ): Boolean {
        if (webClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg)) {
            return true
        }
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    override fun onCloseWindow(window: WebView) {
        if (!webClient.onCloseWindow(window)) {
            super.onCloseWindow(window)
        }
    }

    override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
        if (!webClient.onConsoleMessage(
                per.goweii.anole.ability.ConsoleMessage(
                    message, sourceID, lineNumber,
                    per.goweii.anole.ability.ConsoleMessage.MessageLevel.DEBUG
                )
            )
        ) {
            super.onConsoleMessage(message, lineNumber, sourceID)
        }
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        if (webClient.onConsoleMessage(consoleMessage?.toLibraryConsoleMessage())) {
            return true
        }
        return super.onConsoleMessage(consoleMessage)
    }

    override fun onPermissionRequest(request: PermissionRequest?) {
        if (webClient.onPermissionRequest(request?.toLibraryPermissionRequest())) {
            return
        }
        super.onPermissionRequest(request)
    }

    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        if (webClient.onPermissionRequestCanceled(request?.toLibraryPermissionRequest())) {
            return
        }
        super.onPermissionRequestCanceled(request)
    }

    override fun onGeolocationPermissionsShowPrompt(
        origin: String?,
        callback: GeolocationPermissions.Callback?
    ) {
        if (!webClient.onGeolocationPermissionsShowPrompt(
                origin,
                callback?.toLibraryGeolocationPermissionsCallback()
            )
        ) {
            super.onGeolocationPermissionsShowPrompt(origin, callback)
        }
    }

    override fun onGeolocationPermissionsHidePrompt() {
        if (!webClient.onGeolocationPermissionsHidePrompt()) {
            super.onGeolocationPermissionsHidePrompt()
        }
    }

    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        if (webClient.onShowFileChooser(
                webView,
                filePathCallback?.toLibraryValueCallback(),
                fileChooserParams?.toLibraryFileChooserParams()
            )
        ) {
            return true
        }
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    override fun onReceivedTouchIconUrl(view: WebView, url: String?, precomposed: Boolean) {
        if (!webClient.onReceivedTouchIconUrl(view, url, precomposed)) {
            super.onReceivedTouchIconUrl(view, url, precomposed)
        }
    }

    override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
        if (!webClient.onReceivedIcon(view, icon)) {
            super.onReceivedIcon(view, icon)
        }
    }

    override fun onExceededDatabaseQuota(
        url: String?,
        databaseIdentifier: String?,
        quota: Long,
        estimatedDatabaseSize: Long,
        totalQuota: Long,
        quotaUpdater: WebStorage.QuotaUpdater?
    ) {
        super.onExceededDatabaseQuota(
            url,
            databaseIdentifier,
            quota,
            estimatedDatabaseSize,
            totalQuota,
            quotaUpdater
        )
    }

    override fun onReceivedTitle(view: WebView, title: String?) {
        if (!webClient.onReceivedTitle(view, title)) {
            super.onReceivedTitle(view, title)
        }
    }

    override fun onReachedMaxAppCacheSize(
        requiredStorage: Long,
        quota: Long,
        quotaUpdater: WebStorage.QuotaUpdater?
    ) {
        super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        if (!webClient.onProgressChanged(view, newProgress)) {
            super.onProgressChanged(view, newProgress)
        }
    }

    override fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
        if (!webClient.getVisitedHistory(callback?.toLibraryValueCallback())) {
            super.getVisitedHistory(callback)
        }
    }

    override fun getVideoLoadingProgressView(): View? {
        return webClient.getVideoLoadingProgressView() ?: super.getVideoLoadingProgressView()
    }

    override fun getDefaultVideoPoster(): Bitmap? {
        return webClient.getDefaultVideoPoster() ?: super.getDefaultVideoPoster()
    }
}