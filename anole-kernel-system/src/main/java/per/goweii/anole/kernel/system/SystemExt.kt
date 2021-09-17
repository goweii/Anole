package per.goweii.anole.kernel.system

import android.content.Intent
import android.net.Uri
import android.os.Build
import java.security.PrivateKey
import java.security.cert.X509Certificate

fun per.goweii.anole.ability.WebResourceResponse.toSystemWebResourceResponse(): android.webkit.WebResourceResponse {
    val response = android.webkit.WebResourceResponse(mimeType, encoding, inputStream)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        response.responseHeaders = responseHeaders
        if (statusCode != null && !reasonPhrase.isNullOrEmpty()) {
            try {
                response.setStatusCodeAndReasonPhrase(statusCode!!, reasonPhrase!!)
            } catch (e: Throwable) {
            }
        }
    }
    return response
}

fun android.webkit.WebResourceResponse.toLibraryWebResourceResponse(): per.goweii.anole.ability.WebResourceResponse {
    return per.goweii.anole.ability.WebResourceResponse(
        mimeType,
        encoding,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) statusCode else null,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) reasonPhrase else null,
        data,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) responseHeaders else null
    )
}

fun android.webkit.WebResourceError.toSystemWebResourceError(): per.goweii.anole.ability.WebResourceError? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return null
    return per.goweii.anole.ability.WebResourceError(errorCode.toLibraryErrorCode(), description)
}

fun Int.toLibraryErrorCode(): Int = when (this) {
    android.webkit.WebViewClient.ERROR_AUTHENTICATION -> per.goweii.anole.ability.WebConst.ERROR_AUTHENTICATION
    android.webkit.WebViewClient.ERROR_BAD_URL -> per.goweii.anole.ability.WebConst.ERROR_BAD_URL
    android.webkit.WebViewClient.ERROR_CONNECT -> per.goweii.anole.ability.WebConst.ERROR_CONNECT
    android.webkit.WebViewClient.ERROR_FAILED_SSL_HANDSHAKE -> per.goweii.anole.ability.WebConst.ERROR_FAILED_SSL_HANDSHAKE
    android.webkit.WebViewClient.ERROR_FILE -> per.goweii.anole.ability.WebConst.ERROR_FILE
    android.webkit.WebViewClient.ERROR_FILE_NOT_FOUND -> per.goweii.anole.ability.WebConst.ERROR_FILE_NOT_FOUND
    android.webkit.WebViewClient.ERROR_HOST_LOOKUP -> per.goweii.anole.ability.WebConst.ERROR_HOST_LOOKUP
    android.webkit.WebViewClient.ERROR_IO -> per.goweii.anole.ability.WebConst.ERROR_IO
    android.webkit.WebViewClient.ERROR_PROXY_AUTHENTICATION -> per.goweii.anole.ability.WebConst.ERROR_PROXY_AUTHENTICATION
    android.webkit.WebViewClient.ERROR_REDIRECT_LOOP -> per.goweii.anole.ability.WebConst.ERROR_REDIRECT_LOOP
    android.webkit.WebViewClient.ERROR_TIMEOUT -> per.goweii.anole.ability.WebConst.ERROR_TIMEOUT
    android.webkit.WebViewClient.ERROR_TOO_MANY_REQUESTS -> per.goweii.anole.ability.WebConst.ERROR_TOO_MANY_REQUESTS
    android.webkit.WebViewClient.ERROR_UNKNOWN -> per.goweii.anole.ability.WebConst.ERROR_UNKNOWN
    android.webkit.WebViewClient.ERROR_UNSAFE_RESOURCE -> per.goweii.anole.ability.WebConst.ERROR_UNSAFE_RESOURCE
    android.webkit.WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME -> per.goweii.anole.ability.WebConst.ERROR_UNSUPPORTED_AUTH_SCHEME
    android.webkit.WebViewClient.ERROR_UNSUPPORTED_SCHEME -> per.goweii.anole.ability.WebConst.ERROR_UNSUPPORTED_SCHEME
    else -> per.goweii.anole.ability.WebConst.ERROR_UNKNOWN
}

fun Int.toLibraryThreatType(): Int = when (this) {
    android.webkit.WebViewClient.SAFE_BROWSING_THREAT_BILLING -> per.goweii.anole.ability.WebConst.SAFE_BROWSING_THREAT_BILLING
    android.webkit.WebViewClient.SAFE_BROWSING_THREAT_MALWARE -> per.goweii.anole.ability.WebConst.SAFE_BROWSING_THREAT_MALWARE
    android.webkit.WebViewClient.SAFE_BROWSING_THREAT_PHISHING -> per.goweii.anole.ability.WebConst.SAFE_BROWSING_THREAT_PHISHING
    android.webkit.WebViewClient.SAFE_BROWSING_THREAT_UNKNOWN -> per.goweii.anole.ability.WebConst.SAFE_BROWSING_THREAT_UNKNOWN
    android.webkit.WebViewClient.SAFE_BROWSING_THREAT_UNWANTED_SOFTWARE -> per.goweii.anole.ability.WebConst.SAFE_BROWSING_THREAT_UNWANTED_SOFTWARE
    else -> per.goweii.anole.ability.WebConst.SAFE_BROWSING_THREAT_UNKNOWN
}

fun android.webkit.WebResourceRequest.toLibraryWebResourceRequest(): per.goweii.anole.ability.WebResourceRequest? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return null
    return per.goweii.anole.ability.WebResourceRequest(
        uri = url,
        isForMainFrame = isForMainFrame,
        isRedirect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) isRedirect else false,
        hasGesture = hasGesture(),
        method = method,
        requestHeaders = requestHeaders
    )
}

fun android.webkit.ClientCertRequest.toLibraryClientCertRequest(): per.goweii.anole.ability.ClientCertRequest? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return null
    return object : per.goweii.anole.ability.ClientCertRequest(keyTypes, principals, host, port) {
        override fun proceed(privateKey: PrivateKey?, chain: Array<X509Certificate?>?) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this@toLibraryClientCertRequest.proceed(privateKey, chain)
            }
        }

        override fun ignore() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this@toLibraryClientCertRequest.ignore()
            }
        }

        override fun cancel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this@toLibraryClientCertRequest.cancel()
            }
        }
    }
}

fun android.webkit.HttpAuthHandler.toLibraryHttpAuthHandler(): per.goweii.anole.ability.HttpAuthHandler {
    return object : per.goweii.anole.ability.HttpAuthHandler(looper) {
        override fun useHttpAuthUsernamePassword(): Boolean {
            return this@toLibraryHttpAuthHandler.useHttpAuthUsernamePassword()
        }

        override fun cancel() {
            this@toLibraryHttpAuthHandler.cancel()
        }

        override fun proceed(username: String?, password: String?) {
            this@toLibraryHttpAuthHandler.proceed(username, password)
        }
    }
}

fun android.webkit.SslErrorHandler.toLibrarySslErrorHandler(): per.goweii.anole.ability.SslErrorHandler {
    return object : per.goweii.anole.ability.SslErrorHandler(looper) {
        override fun cancel() {
            this@toLibrarySslErrorHandler.cancel()
        }

        override fun proceed() {
            this@toLibrarySslErrorHandler.proceed()
        }
    }
}

fun android.webkit.WebChromeClient.CustomViewCallback.toLibraryCustomViewCallback(): per.goweii.anole.ability.CustomViewCallback {
    return object : per.goweii.anole.ability.CustomViewCallback {
        override fun onCustomViewHidden() {
            this@toLibraryCustomViewCallback.onCustomViewHidden()
        }
    }
}

fun android.webkit.WebChromeClient.FileChooserParams.toLibraryFileChooserParams(): per.goweii.anole.ability.FileChooserParams? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return null
    return object : per.goweii.anole.ability.FileChooserParams(
        when (mode) {
            android.webkit.WebChromeClient.FileChooserParams.MODE_OPEN -> Mode.MODE_OPEN
            android.webkit.WebChromeClient.FileChooserParams.MODE_OPEN_MULTIPLE -> Mode.MODE_OPEN_MULTIPLE
            android.webkit.WebChromeClient.FileChooserParams.MODE_SAVE -> Mode.MODE_SAVE
            else -> null
        },
        acceptTypes,
        isCaptureEnabled,
        title,
        filenameHint
    ) {
        override fun createIntent(): Intent? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this@toLibraryFileChooserParams.createIntent()
            } else {
                null
            }
        }

        override fun parseResult(resultCode: Int, data: Intent?): Array<Uri>? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                android.webkit.WebChromeClient.FileChooserParams.parseResult(resultCode, data)
            } else {
                null
            }
        }

    }
}

fun android.webkit.ConsoleMessage.MessageLevel.toLibraryConsoleMessageLevel(): per.goweii.anole.ability.ConsoleMessage.MessageLevel {
    return when (this) {
        android.webkit.ConsoleMessage.MessageLevel.TIP ->
            per.goweii.anole.ability.ConsoleMessage.MessageLevel.VERBOSE
        android.webkit.ConsoleMessage.MessageLevel.LOG ->
            per.goweii.anole.ability.ConsoleMessage.MessageLevel.INFO
        android.webkit.ConsoleMessage.MessageLevel.WARNING ->
            per.goweii.anole.ability.ConsoleMessage.MessageLevel.WARNING
        android.webkit.ConsoleMessage.MessageLevel.ERROR ->
            per.goweii.anole.ability.ConsoleMessage.MessageLevel.ERROR
        android.webkit.ConsoleMessage.MessageLevel.DEBUG ->
            per.goweii.anole.ability.ConsoleMessage.MessageLevel.DEBUG
    }
}

fun android.webkit.ConsoleMessage.toLibraryConsoleMessage(): per.goweii.anole.ability.ConsoleMessage {
    return per.goweii.anole.ability.ConsoleMessage(
        this.message(),
        this.sourceId(),
        this.lineNumber(),
        this.messageLevel().toLibraryConsoleMessageLevel()
    )
}

fun android.webkit.GeolocationPermissions.Callback.toLibraryGeolocationPermissionsCallback(): per.goweii.anole.ability.GeolocationPermissions.Callback {
    return object : per.goweii.anole.ability.GeolocationPermissions.Callback {
        override fun invoke(origin: String?, allow: Boolean, retain: Boolean) {
            this@toLibraryGeolocationPermissionsCallback.invoke(origin, allow, retain)
        }
    }
}

fun android.webkit.PermissionRequest.toLibraryPermissionRequest(): per.goweii.anole.ability.PermissionRequest? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) return null
    return object : per.goweii.anole.ability.PermissionRequest(
        this@toLibraryPermissionRequest.origin,
        this@toLibraryPermissionRequest.resources
    ) {
        override fun grant(resources: Array<String>) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this@toLibraryPermissionRequest.grant(resources)
            }
        }

        override fun deny() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this@toLibraryPermissionRequest.deny()
            }
        }
    }
}

fun android.webkit.SafeBrowsingResponse.toLibrarySafeBrowsingResponse(): per.goweii.anole.ability.SafeBrowsingResponse? {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O_MR1) return null
    return object : per.goweii.anole.ability.SafeBrowsingResponse {
        override fun showInterstitial(allowReporting: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                this@toLibrarySafeBrowsingResponse.showInterstitial(allowReporting)
            }
        }

        override fun proceed(report: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                this@toLibrarySafeBrowsingResponse.proceed(report)
            }
        }

        override fun backToSafety(report: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                this@toLibrarySafeBrowsingResponse.backToSafety(report)
            }
        }
    }
}

fun android.webkit.JsResult.toLibraryJsResult(): per.goweii.anole.ability.JsResult {
    return per.goweii.anole.ability.JsResult(
        object : per.goweii.anole.ability.JsResult.ResultReceiver {
            override fun onJsResultComplete(result: per.goweii.anole.ability.JsResult) {
                if (result.result) {
                    this@toLibraryJsResult.confirm()
                } else {
                    this@toLibraryJsResult.cancel()
                }
            }
        })
}

fun android.webkit.JsPromptResult.toLibraryJsPromptResult(): per.goweii.anole.ability.JsPromptResult {
    return per.goweii.anole.ability.JsPromptResult(
        object : per.goweii.anole.ability.JsResult.ResultReceiver {
            override fun onJsResultComplete(result: per.goweii.anole.ability.JsResult) {
                if (result is per.goweii.anole.ability.JsPromptResult) {
                    if (result.result) {
                        this@toLibraryJsPromptResult.confirm(result.stringResult)
                    } else {
                        this@toLibraryJsPromptResult.cancel()
                    }
                } else {
                    if (result.result) {
                        this@toLibraryJsPromptResult.confirm()
                    } else {
                        this@toLibraryJsPromptResult.cancel()
                    }
                }
            }
        })
}

fun <T> android.webkit.ValueCallback<T>.toLibraryValueCallback(): per.goweii.anole.kernel.ValueCallback<T> {
    return object : per.goweii.anole.kernel.ValueCallback<T> {
        override fun onReceiveValue(value: T) {
            this@toLibraryValueCallback.onReceiveValue(value)
        }
    }
}
