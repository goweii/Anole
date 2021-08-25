package per.goweii.anole.ability

import android.content.Intent
import android.net.Uri

abstract class FileChooserParams(
    val mode: Mode?,
    val acceptTypes: Array<String>?,
    val isCaptureEnabled: Boolean,
    val title: CharSequence?,
    val filenameHint: String?
) {
    abstract fun createIntent(): Intent?

    abstract fun parseResult(resultCode: Int, data: Intent?): Array<Uri>?

    enum class Mode {
        MODE_OPEN,
        MODE_OPEN_MULTIPLE,
        MODE_SAVE,
        ;
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FileChooserParams) return false

        if (mode != other.mode) return false
        if (acceptTypes != null) {
            if (other.acceptTypes == null) return false
            if (!acceptTypes.contentEquals(other.acceptTypes)) return false
        } else if (other.acceptTypes != null) return false
        if (isCaptureEnabled != other.isCaptureEnabled) return false
        if (title != other.title) return false
        if (filenameHint != other.filenameHint) return false

        return true
    }

    override fun hashCode(): Int {
        var result = mode?.hashCode() ?: 0
        result = 31 * result + (acceptTypes?.contentHashCode() ?: 0)
        result = 31 * result + isCaptureEnabled.hashCode()
        result = 31 * result + (title?.hashCode() ?: 0)
        result = 31 * result + (filenameHint?.hashCode() ?: 0)
        return result
    }
}