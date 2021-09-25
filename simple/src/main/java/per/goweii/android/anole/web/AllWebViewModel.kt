package per.goweii.android.anole.web

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import per.goweii.android.anole.base.BaseAndroidViewModel
import per.goweii.android.anole.utils.WebToken

class AllWebViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle
) : BaseAndroidViewModel(application) {
    companion object {
        const val CURRENT_ITEM = "currentItem"
    }

    private val _onTouchSharedFlow = MutableSharedFlow<WebToken>()
    val onTouchSharedFlow: SharedFlow<WebToken> = _onTouchSharedFlow
    private val _onRemoveSharedFlow = MutableSharedFlow<WebToken>()
    val onRemoveSharedFlow: SharedFlow<WebToken> = _onRemoveSharedFlow
    private val _webTokenListStateFlow = MutableStateFlow(ArrayList<WebToken>())
    val webTokenListStateFlow = _webTokenListStateFlow

    var currentItem: Int
        get() = savedStateHandle.get<Int>(CURRENT_ITEM) ?: 0
        set(value) {
            savedStateHandle.set<Int>(CURRENT_ITEM, value)
        }

    private val webTokenList get() = _webTokenListStateFlow.value

    fun onTouchedWebFragment(webToken: WebToken) {
        viewModelScope.launch {
            _onTouchSharedFlow.emit(webToken)
        }
    }

    fun onRemoveWebFragment(webToken: WebToken) {
        viewModelScope.launch {
            _onRemoveSharedFlow.emit(webToken)
        }
    }

    fun indexOf(webToken: WebToken): Int {
        return webTokenList.indexOf(webToken)
    }

    fun indexOf(kernelId: Int): Int {
        return webTokenList.indexOfFirst { it.kernelId == kernelId }
    }

    fun addWeb(webToken: WebToken) {
        viewModelScope.launch {
            _webTokenListStateFlow.update {
                ArrayList(it).apply { add(webToken) }
            }
        }
    }

    fun removeWeb(webToken: WebToken) {
        viewModelScope.launch {
            _webTokenListStateFlow.update {
                ArrayList(it).apply { remove(webToken) }
            }
        }
    }

    fun removeWebAt(index: Int) {
        _webTokenListStateFlow.update {
            ArrayList(it).apply { removeAt(index) }
        }
    }
}