package per.goweii.anole.kernel.system

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.view.KeyEvent
import android.webkit.*
import androidx.annotation.RequiresApi

class SystemWebViewClient(
    private val bridgeWebClient: BridgeWebClient
) : WebViewClient() {

    override fun shouldInterceptRequest(view: WebView, url: String?): WebResourceResponse? {
        val reqUri: Uri = Uri.parse(url)
        val response: WebResourceResponse? =
            bridgeWebClient.shouldInterceptRequest(view, reqUri, null, null, null)
        return response ?: super.shouldInterceptRequest(view, url)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        val reqUri = request.url ?: return super.shouldInterceptRequest(view, request)
        val reqHeaders = request.requestHeaders
        val reqMethod = request.method
        val response: WebResourceResponse? =
            bridgeWebClient.shouldInterceptRequest(view, reqUri, reqHeaders, reqMethod, null)
        return response ?: super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        val reqUri: Uri = Uri.parse(url)
        val override = bridgeWebClient.shouldOverrideUrlLoading(view, reqUri, null, null, null)
        return if (override) true
        else super.shouldOverrideUrlLoading(view, url)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val reqUri = request.url ?: return super.shouldOverrideUrlLoading(view, request)
        val reqHeaders = request.requestHeaders
        val reqMethod = request.method
        val override =
            bridgeWebClient.shouldOverrideUrlLoading(view, reqUri, reqHeaders, reqMethod, null)
        return if (override) true
        else super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldOverrideKeyEvent(view: WebView, event: KeyEvent): Boolean {
        if (bridgeWebClient.shouldOverrideKeyEvent(view, event)) {
            return true
        }
        return super.shouldOverrideKeyEvent(view, event)
    }

    override fun onSafeBrowsingHit(
        view: WebView,
        request: WebResourceRequest?,
        threatType: Int,
        callback: SafeBrowsingResponse?
    ) {
        super.onSafeBrowsingHit(view, request, threatType, callback)
        bridgeWebClient.onSafeBrowsingHit(view, request, threatType, callback)
    }

    override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
        super.doUpdateVisitedHistory(view, url, isReload)
        bridgeWebClient.doUpdateVisitedHistory(view, url, isReload)
    }

    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        super.onReceivedError(view, errorCode, description, failingUrl)
        bridgeWebClient.onReceivedError(view, errorCode, description, failingUrl)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        bridgeWebClient.onReceivedError(view, request, error)
    }

    override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        super.onReceivedHttpError(view, request, errorResponse)
        bridgeWebClient.onReceivedHttpError(view, request, errorResponse)
    }

    override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail?): Boolean {
        if (bridgeWebClient.onRenderProcessGone(view, detail)) {
            return true
        }
        return super.onRenderProcessGone(view, detail)
    }

    override fun onReceivedLoginRequest(
        view: WebView,
        realm: String?,
        account: String?,
        args: String?
    ) {
        super.onReceivedLoginRequest(view, realm, account, args)
        bridgeWebClient.onReceivedLoginRequest(view, realm, account, args)
    }

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        super.onPageStarted(view, url, favicon)
        bridgeWebClient.onPageStarted(view, url, favicon)
    }

    override fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
        super.onScaleChanged(view, oldScale, newScale)
        bridgeWebClient.onScaleChanged(view, oldScale, newScale)
    }

    override fun onPageFinished(view: WebView, url: String?) {
        super.onPageFinished(view, url)
        bridgeWebClient.onPageFinished(view, url)
    }

    override fun onPageCommitVisible(view: WebView, url: String?) {
        super.onPageCommitVisible(view, url)
        bridgeWebClient.onPageCommitVisible(view, url)
    }

    override fun onUnhandledKeyEvent(view: WebView, event: KeyEvent?) {
        super.onUnhandledKeyEvent(view, event)
        bridgeWebClient.onUnhandledKeyEvent(view, event)
    }

    override fun onReceivedClientCertRequest(view: WebView, request: ClientCertRequest?) {
        super.onReceivedClientCertRequest(view, request)
        bridgeWebClient.onReceivedClientCertRequest(view, request)
    }

    override fun onReceivedHttpAuthRequest(
        view: WebView,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
        bridgeWebClient.onReceivedHttpAuthRequest(view, handler, host, realm)
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler?, error: SslError?) {
        super.onReceivedSslError(view, handler, error)
        bridgeWebClient.onReceivedSslError(view, handler, error)
    }

    override fun onTooManyRedirects(view: WebView, cancelMsg: Message?, continueMsg: Message?) {
        super.onTooManyRedirects(view, cancelMsg, continueMsg)
        bridgeWebClient.onTooManyRedirects(view, cancelMsg, continueMsg)
    }

    override fun onFormResubmission(view: WebView, dontResend: Message?, resend: Message?) {
        super.onFormResubmission(view, dontResend, resend)
        bridgeWebClient.onFormResubmission(view, dontResend, resend)
    }

    override fun onLoadResource(view: WebView, url: String?) {
        super.onLoadResource(view, url)
        bridgeWebClient.onLoadResource(view, url)
    }
}