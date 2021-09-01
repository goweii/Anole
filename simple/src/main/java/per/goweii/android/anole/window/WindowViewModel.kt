package per.goweii.android.anole.window

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import per.goweii.android.anole.base.BaseAndroidViewModel
import per.goweii.android.anole.home.Bookmark

class WindowViewModel(application: Application) : BaseAndroidViewModel(application) {
    val windowCountLiveData = MutableLiveData(0)
    val goBackEnableLiveData = MutableLiveData(false)
    val goForwardEnableLiveData = MutableLiveData(false)

    private val _goBackOrForwardSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow()
    val goBackOrForwardSharedFlow: SharedFlow<Int> = _goBackOrForwardSharedFlow
    private val _loadUrlSharedFlow: MutableSharedFlow<String?> = MutableSharedFlow()
    val loadUrlSharedFlow: SharedFlow<String?> = _loadUrlSharedFlow
    private val _reloadSharedFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val reloadSharedFlow: SharedFlow<Boolean> = _reloadSharedFlow
    private val _addOrUpdateBookmarkSharedFlow: MutableSharedFlow<Bookmark> = MutableSharedFlow()
    val addOrUpdateBookmarkSharedFlow: SharedFlow<Bookmark> = _addOrUpdateBookmarkSharedFlow
    private val _removeBookmarkSharedFlow: MutableSharedFlow<String?> = MutableSharedFlow()
    val removeBookmarkSharedFlow: SharedFlow<String?> = _removeBookmarkSharedFlow
    private val _loadUrlOnNewWindowSharedFlow: MutableSharedFlow<String?> = MutableSharedFlow()
    val loadUrlOnNewWindowSharedFlow: SharedFlow<String?> = _loadUrlOnNewWindowSharedFlow

    fun addOrUpdateBookmark(bookmark: Bookmark) {
        viewModelScope.launch { _addOrUpdateBookmarkSharedFlow.emit(bookmark) }
    }

    fun removeBookmark(url: String) {
        viewModelScope.launch { _removeBookmarkSharedFlow.emit(url) }
    }

    fun getBackOrForward(step: Int) {
        viewModelScope.launch { _goBackOrForwardSharedFlow.emit(step) }
    }

    fun loadUrl(url: String?) {
        viewModelScope.launch { _loadUrlSharedFlow.emit(url) }
    }

    fun loadUrlOnNewWindow(url: String?) {
        viewModelScope.launch { _loadUrlOnNewWindowSharedFlow.emit(url) }
    }

    fun reload() {
        viewModelScope.launch { _reloadSharedFlow.emit(true) }
    }

    fun stopLoading() {
        viewModelScope.launch { _reloadSharedFlow.emit(false) }
    }
}