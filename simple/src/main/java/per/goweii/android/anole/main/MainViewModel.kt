package per.goweii.android.anole.main

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val windowCountLiveData = MutableLiveData(0)
    val goBackOrForwardLiveData = MutableLiveData(0)
    val progressLiveData = MutableLiveData(0)
    val currUrlLiveData = MutableLiveData<String?>(null)
    val currTitleLiveData = MutableLiveData<String?>(null)
    val currIconLiveData = MutableLiveData<Bitmap?>(null)

    val goBackEnableLiveData = MutableLiveData(false)
    val goForwardEnableLiveData = MutableLiveData(false)
    val loadUrlLiveData = MutableLiveData<String?>(null)
    val reloadLiveData = MutableLiveData(false)
}