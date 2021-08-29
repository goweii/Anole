package per.goweii.android.anole.window

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class WindowViewModel(application: Application) : AndroidViewModel(application) {
    val windowCountLiveData = MutableLiveData(0)
    val progressLiveData = MutableLiveData(0)
    val currUrlLiveData = MutableLiveData<String?>(null)
    val currTitleLiveData = MutableLiveData<String?>(null)
    val currIconLiveData = MutableLiveData<Bitmap?>(null)
    val goBackEnableLiveData = MutableLiveData(false)
    val goForwardEnableLiveData = MutableLiveData(false)

    private val _goBackOrForwardSharedFlow: MutableSharedFlow<Int> = MutableSharedFlow()
    val goBackOrForwardSharedFlow: SharedFlow<Int> = _goBackOrForwardSharedFlow
    private val _loadUrlSharedFlow: MutableSharedFlow<String?> = MutableSharedFlow()
    val loadUrlSharedFlow: SharedFlow<String?> = _loadUrlSharedFlow
    private val _reloadSharedFlow: MutableSharedFlow<Boolean> = MutableSharedFlow()
    val reloadSharedFlow: SharedFlow<Boolean> = _reloadSharedFlow

    fun getBackOrForward(step: Int) {
        viewModelScope.launch { _goBackOrForwardSharedFlow.emit(step) }
    }

    fun loadUrl(url: String?) {
        viewModelScope.launch { _loadUrlSharedFlow.emit(url) }
    }

    fun reload() {
        viewModelScope.launch { _reloadSharedFlow.emit(true) }
    }

    fun stopLoading() {
        viewModelScope.launch { _reloadSharedFlow.emit(false) }
    }
}