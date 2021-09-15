package per.goweii.anole.kernel.system

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.os.Build
import android.os.Message
import android.view.KeyEvent
import android.webkit.*
import androidx.annotation.RequiresApi
import per.goweii.anole.client.WebClient

@Suppress("DEPRECATION")
class SystemWebViewClient(
    private val webClient: WebClient
) : WebViewClient() {
    override fun shouldInterceptRequest(view: WebView, url: String?): WebResourceResponse? {
        return url?.let { Uri.parse(url) }
            ?.let { per.goweii.anole.ability.WebResourceRequest(it) }
            ?.let { webClient.shouldInterceptRequest(view, it) }
            ?.toSystemWebResourceResponse()
            ?: super.shouldInterceptRequest(view, url)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldInterceptRequest(
        view: WebView,
        request: WebResourceRequest
    ): WebResourceResponse? {
        return request.toLibraryWebResourceRequest()
            ?.let { webClient.shouldInterceptRequest(view, it) }
            ?.toSystemWebResourceResponse()
            ?: super.shouldInterceptRequest(view, request)
    }

    override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
        return url?.let { Uri.parse(url) }
            ?.let { per.goweii.anole.ability.WebResourceRequest(it) }
            ?.let { webClient.shouldOverrideUrlLoading(view, it) }
            ?.takeIf { it }
            ?: super.shouldOverrideUrlLoading(view, url)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        return request.toLibraryWebResourceRequest()
            ?.let { webClient.shouldOverrideUrlLoading(view, it) }
            ?.takeIf { it }
            ?: super.shouldOverrideUrlLoading(view, request)
    }

    override fun shouldOverrideKeyEvent(view: WebView, event: KeyEvent): Boolean {
        if (webClient.shouldOverrideKeyEvent(view, event)) {
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
        if (!webClient.onSafeBrowsingHit(
                view,
                request?.toLibraryWebResourceRequest(),
                threatType,
                callback?.toLibrarySafeBrowsingResponse()
            )
        ) {
            super.onSafeBrowsingHit(view, request, threatType, callback)
        }
    }

    override fun doUpdateVisitedHistory(view: WebView, url: String, isReload: Boolean) {
        if (!webClient.doUpdateVisitedHistory(view, url, isReload)) {
            super.doUpdateVisitedHistory(view, url, isReload)
        }
    }

    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String?,
        failingUrl: String?
    ) {
        val request = failingUrl
            ?.let { Uri.parse(it) }
            ?.let { per.goweii.anole.ability.WebResourceRequest(it) }
        val error = per.goweii.anole.ability.WebResourceError(errorCode, description)
        if (!webClient.onReceivedError(view, request, error)) {
            super.onReceivedError(view, errorCode, description, failingUrl)
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onReceivedError(
        view: WebView,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        if (!webClient.onReceivedError(
                view,
                request?.toLibraryWebResourceRequest(),
                error?.toSystemWebResourceError()
            )
        ) {
            super.onReceivedError(view, request, error)
        }
    }

    override fun onReceivedHttpError(
        view: WebView,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ) {
        if (!webClient.onReceivedHttpError(
                view,
                request?.toLibraryWebResourceRequest(),
                errorResponse?.toLibraryWebResourceResponse()
            )
        ) {
            super.onReceivedHttpError(view, request, errorResponse)
        }
    }

    override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail?): Boolean {
        return super.onRenderProcessGone(view, detail)
    }

    override fun onReceivedLoginRequest(
        view: WebView,
        realm: String?,
        account: String?,
        args: String?
    ) {
        if (!webClient.onReceivedLoginRequest(view, realm, account, args)) {
            super.onReceivedLoginRequest(view, realm, account, args)
        }
    }

    override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
        if (!webClient.onPageStarted(view, url, favicon)) {
            super.onPageStarted(view, url, favicon)
        }
    }

    override fun onScaleChanged(view: WebView, oldScale: Float, newScale: Float) {
        if (!webClient.onScaleChanged(view, oldScale, newScale)) {
            super.onScaleChanged(view, oldScale, newScale)
        }
    }

    override fun onPageFinished(view: WebView, url: String?) {
        if (!webClient.onPageFinished(view, url)) {
            super.onPageFinished(view, url)
        }
    }

    override fun onPageCommitVisible(view: WebView, url: String?) {
        if (!webClient.onPageCommitVisible(view, url)) {
            super.onPageCommitVisible(view, url)
        }
    }

    override fun onUnhandledKeyEvent(view: WebView, event: KeyEvent?) {
        if (!webClient.onUnhandledKeyEvent(view, event)) {
            super.onUnhandledKeyEvent(view, event)
        }
    }

    override fun onReceivedClientCertRequest(view: WebView, request: ClientCertRequest?) {
        if (!webClient.onReceivedClientCertRequest(view, request?.toLibraryClientCertRequest())) {
            super.onReceivedClientCertRequest(view, request)
        }
    }

    override fun onReceivedHttpAuthRequest(
        view: WebView,
        handler: HttpAuthHandler?,
        host: String?,
        realm: String?
    ) {
        if (!webClient.onReceivedHttpAuthRequest(
                view,
                handler?.toLibraryHttpAuthHandler(),
                host,
                realm
            )
        ) {
            super.onReceivedHttpAuthRequest(view, handler, host, realm)
        }
    }

    override fun onReceivedSslError(view: WebView, handler: SslErrorHandler?, error: SslError?) {
        if (!webClient.onReceivedSslError(view, handler?.toLibrarySslErrorHandler(), error)) {
            super.onReceivedSslError(view, handler, error)
        }
    }

    override fun onTooManyRedirects(view: WebView, cancelMsg: Message?, continueMsg: Message?) {
        if (!webClient.onTooManyRedirects(view, cancelMsg, continueMsg)) {
            super.onTooManyRedirects(view, cancelMsg, continueMsg)
        }
    }

    override fun onFormResubmission(view: WebView, dontResend: Message?, resend: Message?) {
        if (!webClient.onFormResubmission(view, dontResend, resend)) {
            super.onFormResubmission(view, dontResend, resend)
        }
    }

    override fun onLoadResource(view: WebView, url: String?) {
        if (!webClient.onLoadResource(view, url)) {
            super.onLoadResource(view, url)
        }
    }
}