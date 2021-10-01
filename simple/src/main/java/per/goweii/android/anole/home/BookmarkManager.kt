package per.goweii.android.anole.home

import android.content.Context

class BookmarkManager(context: Context) {
    companion object {
        private const val SP_BOOKMARK = "bookmark"
        private const val SP_BOOKMARK_NAME = "bookmarks"

        @Volatile
        private var sInstance: BookmarkManager? = null

        @Synchronized
        fun getInstance(context: Context): BookmarkManager {
            if (sInstance == null) {
                sInstance = BookmarkManager(context.applicationContext)
            }
            return sInstance!!
        }
    }

    private val sp = context.getSharedPreferences(SP_BOOKMARK, Context.MODE_PRIVATE)
    private val bookmarks = arrayListOf<Bookmark>()

    fun get(): List<Bookmark> {
        if (bookmarks.isEmpty()) {
            sp.getStringSet(SP_BOOKMARK_NAME, null)?.forEach { json ->
                Bookmark.fromJson(json)?.let { bookmark ->
                    bookmarks.add(bookmark)
                }
            }
        }
        return bookmarks
    }

    fun remove(url: String): Bookmark? {
        var bookmark: Bookmark? = null
        val iterator = bookmarks.iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.url == url) {
                bookmark = item
                iterator.remove()
                break
            }
        }
        save()
        return bookmark
    }

    fun add(bookmark: Bookmark): Bookmark {
        val oldBookmark = find(bookmark.url)
        return if (oldBookmark != null) {
            oldBookmark.title = bookmark.title
            if (bookmark.logo != null) {
                oldBookmark.logo = bookmark.logo!!
            }
            save()
            oldBookmark
        } else {
            bookmarks.add(bookmark)
            save()
            bookmark
        }
    }

    fun find(url: String): Bookmark? {
        bookmarks.forEach {
            if (it.url == url) {
                return it
            }
        }
        return null
    }

    fun save() {
        val edit = sp.edit()
        bookmarks.map { it.toJson() }
            .filter { !it.isNullOrBlank() }
            .toSet()
            .let {
                edit.putStringSet(SP_BOOKMARK_NAME, it)
            }
        edit.apply()
    }

}