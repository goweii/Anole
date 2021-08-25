package per.goweii.anole.kernel

import android.graphics.Bitmap

interface WebHistoryItem : Cloneable {
    val url: String?
    val originalUrl: String?
    val title: String?
    val favicon: Bitmap?
    override fun clone(): WebHistoryItem
}

data class WebHistoryItemImpl(
    override val url: String?,
    override val originalUrl: String?,
    override val title: String?,
    override val favicon: Bitmap?
): WebHistoryItem{
    override fun clone(): WebHistoryItem {
        return WebHistoryItemImpl(url, originalUrl, title, favicon)
    }
}