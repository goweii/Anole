package per.goweii.android.anole.main

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import per.goweii.android.anole.base.BaseAndroidViewModel

class MainViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _reloadFlow = MutableSharedFlow<Boolean>()
    val reloadFlow: SharedFlow<Boolean> get() = _reloadFlow

    fun reload() {
        viewModelScope.launch { _reloadFlow.emit(false) }
    }
}