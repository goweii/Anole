package per.goweii.anole.kernel.system

import android.graphics.Bitmap
import android.net.Uri
import android.os.Message
import android.view.View
import android.webkit.*

class SystemWebChromeClient(
        private val bridgeWebClient: BridgeWebClient
) : WebChromeClient() {

    override fun onRequestFocus(view: WebView) {
        super.onRequestFocus(view)
        bridgeWebClient.onRequestFocus(view)
    }

    override fun onJsAlert(
            view: WebView,
            url: String?,
            message: String?,
            result: JsResult?
    ): Boolean {
        if (bridgeWebClient.onJsAlert(view, url, message, result)) {
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
        if (bridgeWebClient.onJsPrompt(view, url, message, defaultValue, result)) {
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
        if (bridgeWebClient.onJsConfirm(view, url, message, result)) {
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
        if (bridgeWebClient.onJsBeforeUnload(view, url, message, result)) {
            return true
        }
        return super.onJsBeforeUnload(view, url, message, result)
    }

    override fun onJsTimeout(): Boolean {
        if (bridgeWebClient.onJsTimeout()) {
            return true
        }
        return super.onJsTimeout()
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)
        bridgeWebClient.onShowCustomView(view, callback)
    }

    override fun onShowCustomView(
            view: View?,
            requestedOrientation: Int,
            callback: CustomViewCallback?
    ) {
        super.onShowCustomView(view, requestedOrientation, callback)
        bridgeWebClient.onShowCustomView(view, callback)
    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        bridgeWebClient.onHideCustomView()
    }

    override fun onCreateWindow(
            view: WebView,
            isDialog: Boolean,
            isUserGesture: Boolean,
            resultMsg: Message
    ): Boolean {
        if (bridgeWebClient.onCreateWindow(view, isDialog, isUserGesture, resultMsg)) {
            return true
        }
        return super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
    }

    override fun onCloseWindow(window: WebView) {
        super.onCloseWindow(window)
        bridgeWebClient.onCloseWindow(window)
    }

    override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
        super.onConsoleMessage(message, lineNumber, sourceID)
        bridgeWebClient.onConsoleMessage(
                ConsoleMessage(
                        message,
                        sourceID,
                        lineNumber,
                        ConsoleMessage.MessageLevel.LOG
                )
        )
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        if (bridgeWebClient.onConsoleMessage(consoleMessage)) {
            return true
        }
        return super.onConsoleMessage(consoleMessage)
    }

    override fun onPermissionRequest(request: PermissionRequest?) {
        if (bridgeWebClient.onPermissionRequest(request)) {
            return
        }
        super.onPermissionRequest(request)
    }

    override fun onPermissionRequestCanceled(request: PermissionRequest?) {
        if (bridgeWebClient.onPermissionRequestCanceled(request)) {
            return
        }
        super.onPermissionRequestCanceled(request)
    }

    override fun onGeolocationPermissionsShowPrompt(
            origin: String?,
            callback: GeolocationPermissions.Callback?
    ) {
        super.onGeolocationPermissionsShowPrompt(origin, callback)
        bridgeWebClient.onGeolocationPermissionsShowPrompt(origin, callback)
    }

    override fun onGeolocationPermissionsHidePrompt() {
        super.onGeolocationPermissionsHidePrompt()
        bridgeWebClient.onGeolocationPermissionsHidePrompt()
    }

    override fun onShowFileChooser(
            webView: WebView,
            filePathCallback: ValueCallback<Array<Uri>>?,
            fileChooserParams: FileChooserParams?
    ): Boolean {
        if (bridgeWebClient.onShowFileChooser(webView, filePathCallback, fileChooserParams)) {
            return true
        }
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    override fun onReceivedTouchIconUrl(view: WebView, url: String?, precomposed: Boolean) {
        super.onReceivedTouchIconUrl(view, url, precomposed)
        bridgeWebClient.onReceivedTouchIconUrl(view, url, precomposed)
    }

    override fun onReceivedIcon(view: WebView, icon: Bitmap?) {
        super.onReceivedIcon(view, icon)
        bridgeWebClient.onReceivedIcon(view, icon)
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
        super.onReceivedTitle(view, title)
        bridgeWebClient.onReceivedTitle(view, title)
    }

    override fun onReachedMaxAppCacheSize(
            requiredStorage: Long,
            quota: Long,
            quotaUpdater: WebStorage.QuotaUpdater?
    ) {
        super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
    }

    override fun onProgressChanged(view: WebView, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
        bridgeWebClient.onProgressChanged(view, newProgress)
    }

    override fun getVisitedHistory(callback: ValueCallback<Array<String>>?) {
        super.getVisitedHistory(callback)
        bridgeWebClient.getVisitedHistory(callback)
    }

    override fun getVideoLoadingProgressView(): View? {
        return bridgeWebClient.getVideoLoadingProgressView() ?: super.getVideoLoadingProgressView()
    }

    override fun getDefaultVideoPoster(): Bitmap? {
        return bridgeWebClient.getDefaultVideoPoster() ?: super.getDefaultVideoPoster()
    }
}