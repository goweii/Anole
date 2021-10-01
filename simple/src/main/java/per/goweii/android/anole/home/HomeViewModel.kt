package per.goweii.android.anole.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import per.goweii.android.anole.base.BaseAndroidViewModel
import java.util.*

class HomeViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _bookmarkFlow = MutableStateFlow<List<Bookmark>>(emptyList())
    val bookmarkFlow: StateFlow<List<Bookmark>> get() = _bookmarkFlow

    init {
        refreshBookmark()
    }

    fun addBookmark(bookmark: Bookmark) {
        BookmarkManager.getInstance(getApplication()).add(bookmark)
        refreshBookmark()
    }

    fun removeBookmark(url: String?) {
        url ?: return
        BookmarkManager.getInstance(getApplication()).remove(url)
        refreshBookmark()
    }

    fun swapBookmark(from: Bookmark, to: Bookmark) {
        val list = BookmarkManager.getInstance(getApplication()).get()
        val fromIndex = list.indexOf(from).takeIf { it >= 0 } ?: return
        val toIndex = list.indexOf(to).takeIf { it >= 0 } ?: return
        Collections.swap(list, fromIndex, toIndex)
        BookmarkManager.getInstance(getApplication()).save()
    }

    fun refreshBookmark() {
        viewModelScope.launch {
            val list = BookmarkManager.getInstance(getApplication()).get().reversed()
            _bookmarkFlow.emit(list)
        }
    }
}