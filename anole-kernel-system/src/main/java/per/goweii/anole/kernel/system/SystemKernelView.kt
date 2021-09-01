package per.goweii.anole.kernel.system

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Message
import android.print.PrintDocumentAdapter
import android.util.AttributeSet
import android.webkit.WebView
import androidx.annotation.RequiresApi
import per.goweii.anole.kernel.*
import per.goweii.anole.view.KernelView

class SystemKernelView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : KernelView(context, attrs, defStyleAttr) {
    override val webView: WebView = WebView(context)

    init {
        addView(webView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        setDownloadListener(webClient)
        val bridgeWebClient = BridgeWebClient(webClient)
        webView.webViewClient = SystemWebViewClient(bridgeWebClient)
        webView.webChromeClient = SystemWebChromeClient(bridgeWebClient)
    }

    override val hitTestResult: HitTestResult
        get() {
            val result = webView.hitTestResult
            return HitTestResult(result.type, result.extra)
        }

    override val settings: WebSettings = SystemWetSettings(webView.settings)

    override val canGoBack: Boolean
        get() = webView.canGoBack()

    override val canGoForward: Boolean
        get() = webView.canGoForward()

    override val url: String?
        get() = webView.url

    override val originalUrl: String?
        get() = webView.originalUrl

    override val title: String?
        get() = webView.title

    override val favicon: Bitmap?
        get() = webView.favicon

    override val progress: Int
        get() = webView.progress

    override val contentHeight: Int
        get() = webView.contentHeight

    override val isPrivateBrowsingEnabled: Boolean
        get() = webView.isPrivateBrowsingEnabled

    override fun loadUrl(url: String, additionalHttpHeaders: Map<String?, String?>) {
        webView.loadUrl(url, additionalHttpHeaders)
    }

    override fun loadUrl(url: String) {
        webView.loadUrl(url)
    }

    override fun postUrl(url: String, postData: ByteArray) {
        webView.postUrl(url, postData)
    }

    override fun loadData(data: String, mimeType: String?, encoding: String?) {
        webView.loadData(data, mimeType, encoding)
    }

    override fun loadDataWithBaseURL(
        baseUrl: String?,
        data: String,
        mimeType: String?,
        encoding: String?,
        historyUrl: String?
    ) {
        webView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun evaluateJavascript(script: String, resultCallback: ValueCallback<String?>?) {
        webView.evaluateJavascript(script) {
            resultCallback?.onReceiveValue(it)
        }
    }

    override fun saveWebArchive(filename: String) {
        webView.saveWebArchive(filename)
    }

    override fun saveWebArchive(
        basename: String,
        autoname: Boolean,
        callback: ValueCallback<String?>?
    ) {
        webView.saveWebArchive(basename, autoname) { value ->
            callback?.onReceiveValue(value)
        }
    }

    override fun stopLoading() {
        webView.stopLoading()
    }

    override fun reload() {
        webView.reload()
    }

    override fun goBack() {
        webView.goBack()
    }

    override fun goForward() {
        webView.goForward()
    }

    override fun canGoBackOrForward(steps: Int): Boolean {
        return webView.canGoBackOrForward(steps)
    }

    override fun goBackOrForward(steps: Int) {
        webView.goBackOrForward(steps)
    }

    override fun pageUp(top: Boolean): Boolean {
        return webView.pageUp(top)
    }

    override fun pageDown(bottom: Boolean): Boolean {
        return webView.pageDown(bottom)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun postVisualStateCallback(
        requestId: Long,
        callback: VisualStateCallback
    ) {
        webView.postVisualStateCallback(requestId, object : WebView.VisualStateCallback() {
            override fun onComplete(requestId: Long) {
                callback.onComplete(requestId)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun createPrintDocumentAdapter(documentName: String): PrintDocumentAdapter {
        return webView.createPrintDocumentAdapter(documentName)
    }

    override fun setInitialScale(scaleInPercent: Int) {
        webView.setInitialScale(scaleInPercent)
    }

    override fun invokeZoomPicker() {
        webView.invokeZoomPicker()
    }

    override fun requestFocusNodeHref(hrefMsg: Message?) {
        webView.requestFocusNodeHref(hrefMsg)
    }

    override fun requestImageRef(msg: Message) {
        webView.requestImageRef(msg)
    }

    override fun pauseTimers() {
        webView.pauseTimers()
    }

    override fun resumeTimers() {
        webView.resumeTimers()
    }

    override fun onPause() {
        webView.onPause()
    }

    override fun onResume() {
        webView.onResume()
    }

    override fun destroy() {
        webView.removeAllViews()
        webView.clearView()
        webView.destroy()
    }

    override fun freeMemory() {
        webView.freeMemory()
    }

    override fun clearCache(includeDiskFiles: Boolean) {
        webView.clearCache(includeDiskFiles)
    }

    override fun clearFormData() {
        webView.clearFormData()
    }

    override fun clearHistory() {
        webView.clearHistory()
    }

    override fun clearSslPreferences() {
        webView.clearSslPreferences()
    }

    override fun copyBackForwardList(): WebBackForwardList {
        val copyBackForwardList = webView.copyBackForwardList()
        val size = copyBackForwardList.size
        val currIndex = copyBackForwardList.currentIndex
        val currItem = copyBackForwardList.currentItem?.let {
            WebHistoryItemImpl(
                url = it.url,
                originalUrl = it.originalUrl,
                title = it.title,
                favicon = it.favicon
            )
        }
        val items = arrayListOf<WebHistoryItem>()
        for (i in 0 until copyBackForwardList.size) {
            val itemAtIndex = copyBackForwardList.getItemAtIndex(i)
            items.add(
                WebHistoryItemImpl(
                    url = itemAtIndex.url,
                    originalUrl = itemAtIndex.originalUrl,
                    title = itemAtIndex.title,
                    favicon = itemAtIndex.favicon
                )
            )
        }
        return WebBackForwardListImpl(
            size = size,
            currentIndex = currIndex,
            currentItem = currItem,
            items = items
        )
    }

    override fun setFindListener(listener: FindListener?) {
        if (listener == null) {
            webView.setFindListener(null)
        } else {
            webView.setFindListener { activeMatchOrdinal, numberOfMatches, isDoneCounting ->
                listener.onFindResultReceived(activeMatchOrdinal, numberOfMatches, isDoneCounting)
            }
        }
    }

    override fun findNext(forward: Boolean) {
        webView.findNext(forward)
    }

    override fun clearMatches() {
        webView.clearMatches()
    }

    override fun documentHasImages(response: Message) {
        webView.documentHasImages(response)
    }

    override fun setDownloadListener(listener: DownloadListener?) {
        if (listener == null) {
            webView.setDownloadListener(null)
        } else {
            webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
                listener.onDownloadStart(
                    url,
                    userAgent,
                    contentDisposition,
                    mimetype,
                    contentLength
                )
            }
        }
    }

    @SuppressLint("JavascriptInterface", "AddJavascriptInterface")
    override fun addJavascriptInterface(obj: Any, name: String) {
        webView.addJavascriptInterface(obj, name)
    }

    override fun removeJavascriptInterface(name: String) {
        webView.removeJavascriptInterface(name)
    }
}