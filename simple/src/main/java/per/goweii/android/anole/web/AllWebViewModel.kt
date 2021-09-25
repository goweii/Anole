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

    private val _webTokenListStateFlow = MutableStateFlow(ArrayList<WebToken>())
    val webTokenListStateFlow = _webTokenListStateFlow
    private val _switchWebSharedFlow = MutableSharedFlow<WebToken>()
    val switchWebSharedFlow: SharedFlow<WebToken> = _switchWebSharedFlow

    var currentItem: Int
        get() = savedStateHandle.get<Int>(CURRENT_ITEM) ?: 0
        set(value) {
            savedStateHandle.set<Int>(CURRENT_ITEM, value)
        }

    private val webTokenList get() = _webTokenListStateFlow.value

    fun indexOf(webToken: WebToken): Int {
        return webTokenList.indexOf(webToken)
    }

    fun switchWeb(webToken: WebToken) {
        viewModelScope.launch {
            _switchWebSharedFlow.emit(webToken)
        }
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
            var removedWebToken: WebToken? = null
            _webTokenListStateFlow.update { oldList ->
                val newList = ArrayList(oldList)
                newList.remove(webToken)
                removedWebToken = oldList.find { webToken == it }
                newList
            }
            val parentId = removedWebToken?.parentKernelId ?: return@launch
            webTokenList.find { it.kernelId == parentId }
                ?.let { switchWeb(it) }
        }
    }
}