package per.goweii.anole.kernel

import java.io.Serializable
import java.util.*

interface WebBackForwardList : Cloneable, Serializable {
    val currentItem: WebHistoryItem?
    val currentIndex: Int
    fun getItemAtIndex(index: Int): WebHistoryItem?
    val size: Int
    override fun clone(): WebBackForwardList
}

class WebBackForwardListImpl(
    override val size: Int,
    override val currentIndex: Int,
    override val currentItem: WebHistoryItem?,
    private val items: List<WebHistoryItem>
) : WebBackForwardList {

    override fun getItemAtIndex(index: Int): WebHistoryItem? {
        return items.getOrNull(index)
    }

    override fun clone(): WebBackForwardList {
        val copiedItems = ArrayList<WebHistoryItem>(size)
        Collections.copy(copiedItems, items)
        return WebBackForwardListImpl(size, currentIndex, currentItem, copiedItems)
    }
}