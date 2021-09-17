package per.goweii.anole.ability.impl

import android.net.http.SslError
import android.view.View
import per.goweii.anole.ability.*

class CustomErrorPageAbility(
    private val errorPageAsset: String?
) : WebAbility() {
    override fun onReceivedError(
        webView: View,
        request: WebResourceRequest?,
        error: WebResourceError?
    ): Boolean {
        error ?: return false
        request ?: return false
        if (!request.isForMainFrame) return false
        return when (error.errorCode) {
            WebConst.ERROR_AUTHENTICATION -> false
            WebConst.ERROR_BAD_URL -> loadErrorPage(errorPageAsset)
            WebConst.ERROR_CONNECT -> false
            WebConst.ERROR_FAILED_SSL_HANDSHAKE -> false
            WebConst.ERROR_FILE -> false
            WebConst.ERROR_FILE_NOT_FOUND -> false
            WebConst.ERROR_HOST_LOOKUP -> loadErrorPage(errorPageAsset)
            WebConst.ERROR_IO -> false
            WebConst.ERROR_PROXY_AUTHENTICATION -> false
            WebConst.ERROR_REDIRECT_LOOP -> false
            WebConst.ERROR_TIMEOUT -> loadErrorPage(errorPageAsset)
            WebConst.ERROR_TOO_MANY_REQUESTS -> false
            WebConst.ERROR_UNKNOWN -> false
            WebConst.ERROR_UNSAFE_RESOURCE -> false
            WebConst.ERROR_UNSUPPORTED_AUTH_SCHEME -> false
            WebConst.ERROR_UNSUPPORTED_SCHEME -> false
            else -> false
        }
    }

    override fun onReceivedHttpError(
        webView: View,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?
    ): Boolean {
        return false
    }

    override fun onReceivedSslError(
        webView: View,
        handler: SslErrorHandler?,
        error: SslError?
    ): Boolean {
        return false
    }

    private fun loadErrorPage(assetPage: String?): Boolean {
        val kernel = kernel ?: return false
        val url = assetPage?.toAssetUrl() ?: return false
        kernel.loadUrl("about:blank")
        kernel.loadUrl(url)
        return true
    }

    private fun String.toAssetUrl(): String {
        return "file:///android_asset/$this"
    }
}