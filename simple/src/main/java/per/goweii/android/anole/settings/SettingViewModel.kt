package per.goweii.android.anole.settings

import android.app.Application
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import per.goweii.android.anole.base.BaseAndroidViewModel
import per.goweii.android.anole.utils.DefSearch

class SettingViewModel(application: Application) : BaseAndroidViewModel(application) {
    private val _defSearchFlow = MutableSharedFlow<DefSearch.CustomSearch>()
    val defSearchFlow: SharedFlow<DefSearch.CustomSearch> get() = _defSearchFlow

    fun reloadDefSearch() {
        viewModelScope.launch {
            _defSearchFlow.emit(
                DefSearch.getInstance(getApplication()).getDefSearch()
            )
        }
    }
}