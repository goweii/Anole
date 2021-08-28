package per.goweii.android.anole.utils

import android.net.Uri

@Suppress("MemberVisibilityCanBePrivate", "CanBeParameter", "unused")
class Url(val originalUrl: String) {
    private val uri = Uri.parse(originalUrl)

    val maybeUrl: Boolean get() = isUrl || !host.isNullOrBlank()

    val isUrl: Boolean get() = isHttp || isHttps
    val isHttp: Boolean get() = HTTP == uri.scheme
    val isHttps: Boolean get() = HTTPS == uri.scheme

    val scheme: String? get() = uri.scheme
    val host: String? get() = uri.host

    override fun toString(): String {
        return uri.toString()
    }

    fun toUrl(): String? {
        if (isUrl) {
            return uri.toString()
        }
        if (maybeUrl) {
            return uri.buildUpon()
                .scheme(HTTPS)
                .toString()
        }
        return null
    }

    companion object {
        const val HTTP: String = "http"
        const val HTTPS: String = "https"

        fun isUrl(url: String?): Boolean {
            if (url.isNullOrBlank()) {
                return false
            }
            val uri = Uri.parse(url)
            if (HTTP == uri.scheme || HTTPS == uri.scheme) {
                return true
            }
            return false
        }

        fun parse(url: String?): Url {
            return Url(url?.trim() ?: "")
        }
    }
}