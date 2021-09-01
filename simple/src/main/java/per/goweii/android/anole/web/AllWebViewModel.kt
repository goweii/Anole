package per.goweii.android.anole.web

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import per.goweii.android.anole.base.BaseAndroidViewModel

class AllWebViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _onTouchSharedFlow = MutableSharedFlow<WebFragment>()
    val onTouchSharedFlow = _onTouchSharedFlow
    private val _onRemoveSharedFlow = MutableSharedFlow<WebFragment>()
    val onRemoveSharedFlow = _onRemoveSharedFlow

    fun onTouchedWebFragment(webFragment: WebFragment) {
        viewModelScope.launch {
            _onTouchSharedFlow.emit(webFragment)
        }
    }

    fun onRemoveWebFragment(webFragment: WebFragment) {
        viewModelScope.launch {
            _onRemoveSharedFlow.emit(webFragment)
        }
    }
}