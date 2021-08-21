package per.goweii.anole.kernel.system

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.os.Message
import android.print.PrintDocumentAdapter
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import per.goweii.anole.client.WebClient
import per.goweii.anole.kernel.*

class SystemWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), WebKernel {

    private val webView: WebView = WebView(context)

    init {
        addView(webView, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    override fun addView(child: View?) {
        if (webView != child) return
        super.addView(child)
    }

    override fun addView(child: View?, index: Int) {
        if (webView != child) return
        super.addView(child, index)
    }

    override fun addView(child: View?, params: ViewGroup.LayoutParams?) {
        if (webView != child) return
        super.addView(child, params)
    }

    override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
        if (webView != child) return
        super.addView(child, index, params)
    }

    override fun addView(child: View?, width: Int, height: Int) {
        if (webView != child) return
        super.addView(child, width, height)
    }

    override fun addViewInLayout(
        child: View?,
        index: Int,
        params: ViewGroup.LayoutParams?
    ): Boolean {
        if (webView != child) return false
        return super.addViewInLayout(child, index, params)
    }

    override fun addViewInLayout(
        child: View?,
        index: Int,
        params: ViewGroup.LayoutParams?,
        preventRequestLayout: Boolean
    ): Boolean {
        if (webView != child) return false
        return super.addViewInLayout(child, index, params, preventRequestLayout)
    }

    override val kernelView: View get() = this

    override var webClient: WebClient? = null
        set(value) {
            if (webClient == value) return
            field = value
            val mixedWebClient = value?.let { BridgeWebClient(it) }
            mixedWebClient?.let {
                webView.webViewClient = SystemWebViewClient(mixedWebClient)
                webView.webChromeClient = SystemWebChromeClient(mixedWebClient)
            }
        }

    override val hitTestResult: HitTestResult
        get() {
            val result = webView.hitTestResult
            return HitTestResult(result.type, result.extra)
        }

    override val settings: WebSettings = SystemWetSettings(webView.settings)

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

    override fun canGoBack(): Boolean {
        return webView.canGoBack()
    }

    override fun goBack() {
        webView.goBack()
    }

    override fun canGoForward(): Boolean {
        return webView.canGoForward()
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

    override fun isPrivateBrowsingEnabled(): Boolean {
        return webView.isPrivateBrowsingEnabled
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

    override fun getUrl(): String? {
        return webView.url
    }

    override fun getOriginalUrl(): String? {
        return webView.originalUrl
    }

    override fun getTitle(): String? {
        return webView.title
    }

    override fun getFavicon(): Bitmap? {
        return webView.favicon
    }

    override fun getProgress(): Int {
        return webView.progress
    }

    override fun getContentHeight(): Int {
        return webView.contentHeight
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
        webView.setFindListener { i, i2, b ->
            listener?.onFindResultReceived(i, i2, b)
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
        webView.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            listener?.onDownloadStart(url, userAgent, contentDisposition, mimetype, contentLength)
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