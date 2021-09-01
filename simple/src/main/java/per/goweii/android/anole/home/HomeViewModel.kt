package per.goweii.android.anole.home

import android.app.Application
import kotlinx.coroutines.flow.flow
import per.goweii.android.anole.base.BaseAndroidViewModel

class HomeViewModel(application: Application) : BaseAndroidViewModel(application) {
    val bookmarkLiveData = flow {
        val list = BookmarkManager.getInstance(getApplication()).get()
        emit(list)
    }
}