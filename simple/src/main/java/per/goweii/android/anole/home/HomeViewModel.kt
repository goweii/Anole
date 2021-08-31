package per.goweii.android.anole.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.flow

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    val bookmarkLiveData = flow {
        val list = BookmarkManager.getInstance(getApplication()).get()
        emit(list)
    }
}