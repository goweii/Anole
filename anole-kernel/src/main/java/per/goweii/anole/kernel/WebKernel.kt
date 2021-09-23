package per.goweii.anole.kernel

import android.graphics.Bitmap
import android.os.Message
import android.print.PrintDocumentAdapter
import android.view.View
import android.view.ViewGroup
import per.goweii.anole.client.WebClient

interface WebKernel {
    companion object {
        val View.webKernel: WebKernel?
            get() {
                var view: View? = this
                while (view != null) {
                    if (view is WebKernel) {
                        return view
                    }
                    view = view.parent as? ViewGroup?
                }
                return null
            }
    }

    val webView: View

    val kernelView: View

    val webClient: WebClient

    val hitTestResult: HitTestResult

    val settings: WebSettings

    val url: String?

    val originalUrl: String?

    val title: String?

    val favicon: Bitmap?

    val progress: Int

    val contentHeight: Int

    val canGoBack: Boolean

    val canGoForward: Boolean

    val isPrivateBrowsingEnabled: Boolean

    fun loadUrl(url: String, additionalHttpHeaders: Map<String?, String?>)

    fun loadUrl(url: String)

    fun postUrl(url: String, postData: ByteArray)

    fun loadData(data: String, mimeType: String?, encoding: String?)

    fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    )

    fun evaluateJavascript(script: String, resultCallback: ValueCallback<String?>?)

    fun saveWebArchive(filename: String)

    fun saveWebArchive(basename: String, autoname: Boolean, callback: ValueCallback<String?>?)

    fun stopLoading()

    fun reload()

    fun goBack()

    fun goForward()

    fun canGoBackOrForward(steps: Int): Boolean

    fun goBackOrForward(steps: Int)

    fun pageUp(top: Boolean): Boolean

    fun pageDown(bottom: Boolean): Boolean

    fun postVisualStateCallback(requestId: Long, callback: VisualStateCallback)

    fun createPrintDocumentAdapter(documentName: String): PrintDocumentAdapter

    fun setInitialScale(scaleInPercent: Int)

    fun invokeZoomPicker()

    fun requestFocusNodeHref(hrefMsg: Message?)

    fun requestImageRef(msg: Message)

    fun pauseTimers()

    fun resumeTimers()

    fun onPause()

    fun onResume()

    fun destroy()

    fun freeMemory()

    fun clearCache(includeDiskFiles: Boolean)

    fun clearFormData()

    fun clearHistory()

    fun clearSslPreferences()

    fun copyBackForwardList(): WebBackForwardList

    fun setFindListener(listener: FindListener?)

    fun findNext(forward: Boolean)

    fun clearMatches()

    fun documentHasImages(response: Message)

    fun setDownloadListener(listener: DownloadListener?)

    fun addJavascriptInterface(obj: Any, name: String)

    fun removeJavascriptInterface(name: String)
}