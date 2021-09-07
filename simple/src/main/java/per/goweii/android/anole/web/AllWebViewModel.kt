package per.goweii.android.anole.web

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import per.goweii.android.anole.base.BaseAndroidViewModel
import per.goweii.android.anole.utils.WebInitConfig

class AllWebViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _onTouchSharedFlow = MutableSharedFlow<WebInitConfig>()
    val onTouchSharedFlow = _onTouchSharedFlow
    private val _onRemoveSharedFlow = MutableSharedFlow<WebInitConfig>()
    val onRemoveSharedFlow = _onRemoveSharedFlow

    fun onTouchedWebFragment(webInitConfig: WebInitConfig) {
        viewModelScope.launch {
            _onTouchSharedFlow.emit(webInitConfig)
        }
    }

    fun onRemoveWebFragment(webInitConfig: WebInitConfig) {
        viewModelScope.launch {
            _onRemoveSharedFlow.emit(webInitConfig)
        }
    }
}