package per.goweii.android.anole.window

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import per.goweii.android.anole.base.BaseAndroidViewModel
import per.goweii.android.anole.home.Bookmark

class WindowViewModel(application: Application) : BaseAndroidViewModel(application) {
    val windowCountLiveData = MutableLiveData(0)

    private val _addOrUpdateBookmarkSharedFlow: MutableSharedFlow<Bookmark> = MutableSharedFlow()
    val addOrUpdateBookmarkSharedFlow: SharedFlow<Bookmark> = _addOrUpdateBookmarkSharedFlow
    private val _removeBookmarkSharedFlow: MutableSharedFlow<String?> = MutableSharedFlow()
    val removeBookmarkSharedFlow: SharedFlow<String?> = _removeBookmarkSharedFlow
    private val _newWindowSharedFlow: MutableSharedFlow<String?> = MutableSharedFlow()
    val newWindowSharedFlow: SharedFlow<String?> = _newWindowSharedFlow
    private val _switchChoiceModeSharedFlow: MutableSharedFlow<Boolean?> = MutableSharedFlow()
    val switchChoiceModeSharedFlow: SharedFlow<Boolean?> = _switchChoiceModeSharedFlow
    private val _homeStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(true)
    val homeStateFlow: StateFlow<Boolean> = _homeStateFlow

    fun addOrUpdateBookmark(bookmark: Bookmark) {
        viewModelScope.launch { _addOrUpdateBookmarkSharedFlow.emit(bookmark) }
    }

    fun removeBookmark(url: String) {
        viewModelScope.launch { _removeBookmarkSharedFlow.emit(url) }
    }

    fun newWindow(url: String?) {
        showWeb()
        viewModelScope.launch { _newWindowSharedFlow.emit(url) }
    }

    fun switchChoiceMode(enterOrExit: Boolean?) {
        showWeb()
        viewModelScope.launch { _switchChoiceModeSharedFlow.emit(enterOrExit) }
    }

    fun showHome() {
        viewModelScope.launch { _homeStateFlow.emit(true) }
    }

    fun showWeb() {
        viewModelScope.launch { _homeStateFlow.emit(false) }
    }
}