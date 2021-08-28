package per.goweii.android.anole.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import org.json.JSONObject
import java.io.ByteArrayOutputStream

data class Bookmark(
    val url: String,
    var title: String,
    var logo: Bitmap?
) {
    fun toJson(): String? {
        return toJson(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Bookmark
        if (url != other.url) return false
        return true
    }

    override fun hashCode(): Int {
        return url.hashCode()
    }


    companion object {
        private const val KEY_URL = "url"
        private const val KEY_TITLE = "title"
        private const val KEY_LOGO = "logo"

        fun fromJson(json: String?): Bookmark? {
            if (json.isNullOrBlank()) return null
            return try {
                JSONObject(json).run {
                    val url = getString(KEY_URL)
                    val title = getString(KEY_TITLE)
                    val logo = getString(KEY_LOGO)
                    Bookmark(url, title, decodeLogoFromBase64(logo))
                }
            } catch (e: Exception) {
                null
            }
        }

        fun toJson(obj: Bookmark?): String? {
            obj ?: return null
            return try {
                JSONObject().apply {
                    put(KEY_URL, obj.url)
                    put(KEY_TITLE, obj.title)
                    put(KEY_LOGO, encodeLogoToBase64(obj.logo))
                }.toString()
            } catch (e: Exception) {
                null
            }
        }

        fun encodeLogoToBase64(logo: Bitmap?): String? {
            logo ?: return null
            if (logo.isRecycled) return null
            return try {
                ByteArrayOutputStream().use {
                    logo.compress(Bitmap.CompressFormat.PNG, 100, it)
                    it.flush()
                    Base64.encodeToString(it.toByteArray(), Base64.DEFAULT)
                }
            } catch (e: Exception) {
                null
            }
        }

        fun decodeLogoFromBase64(base64: String?): Bitmap? {
            if (base64.isNullOrBlank()) return null
            return try {
                Base64.decode(base64, Base64.DEFAULT).let {
                    BitmapFactory.decodeByteArray(it, 0, it.size)
                }
            } catch (e: Exception) {
                null
            }
        }
    }
}