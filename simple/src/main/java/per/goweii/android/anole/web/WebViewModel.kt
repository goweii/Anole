package per.goweii.android.anole.web

import android.app.Application
import androidx.lifecycle.SavedStateHandle
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseAndroidViewModel
import per.goweii.android.anole.utils.WebInstance
import per.goweii.android.anole.utils.WebToken
import per.goweii.anole.kernel.WebKernel

class WebViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : BaseAndroidViewModel(application) {
    companion object {
        const val ARG_WEB_TOKEN = "web_token"
    }

    val webToken: WebToken = savedStateHandle.get<WebToken>(ARG_WEB_TOKEN)!!

    val webKernel: WebKernel = WebInstance.getInstance(application).obtain(webToken.kernelId)
        .apply {
            if (url == null && !webToken.subsidiary) {
                loadUrl(webToken.initialUrl ?: application.getString(R.string.initial_url))
            }
        }

    override fun onCleared() {
        super.onCleared()
        WebInstance.getInstance(getApplication()).release(webToken.kernelId)
        webKernel.destroy()
    }
}