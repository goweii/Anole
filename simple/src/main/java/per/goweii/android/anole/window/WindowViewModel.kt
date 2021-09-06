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

    private val _goBackOrForwardSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow()
    val goBackOrForwardSharedFlow: SharedFlow<Int> = _goBackOrForwardSharedFlow
    private val _loadUrlSharedFlow: MutableSharedFlow<String?> = MutableSharedFlow()
    val loadUrlSharedFlow: SharedFlow<String?> = _loadUrlSharedFlow
    private val _addOrUpdateBookmarkSharedFlow: MutableSharedFlow<Bookmark> = MutableSharedFlow()
    val addOrUpdateBookmarkSharedFlow: SharedFlow<Bookmark> = _addOrUpdateBookmarkSharedFlow
    private val _removeBookmarkSharedFlow: MutableSharedFlow<String?> = MutableSharedFlow()
    val removeBookmarkSharedFlow: SharedFlow<String?> = _removeBookmarkSharedFlow
    private val _loadUrlOnNewWindowSharedFlow: MutableSharedFlow<String?> = MutableSharedFlow()
    val loadUrlOnNewWindowSharedFlow: SharedFlow<String?> = _loadUrlOnNewWindowSharedFlow
    private val _switchChoiceModeSharedFlow: MutableSharedFlow<Boolean?> = MutableSharedFlow()
    val switchChoiceModeSharedFlow: SharedFlow<Boolean?> = _switchChoiceModeSharedFlow
    private val _showHomeSharedFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val showHomeModeSharedFlow: SharedFlow<Boolean> = _showHomeSharedFlow
    private val _showWebSharedFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val showWebModeSharedFlow: SharedFlow<Boolean> = _showWebSharedFlow

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

    fun switchChoiceMode(enterOrExit: Boolean?) {
        viewModelScope.launch { _switchChoiceModeSharedFlow.emit(enterOrExit) }
    }

    fun showHome() {
        viewModelScope.launch { _showHomeSharedFlow.emit(false) }
    }

    fun showWeb() {
        viewModelScope.launch { _showWebSharedFlow.emit(false) }
    }
}