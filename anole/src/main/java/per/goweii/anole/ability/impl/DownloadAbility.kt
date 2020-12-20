package per.goweii.anole.ability.impl

import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import per.goweii.anole.ability.AnoleAbility
import per.goweii.anole.view.AnoleView
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.*
import java.util.regex.Pattern
import kotlin.text.StringBuilder

class DownloadAbility(
        private val allowedOverRoaming: Boolean = false,
        private val allowedMobileNetwork: Boolean = false,
        private val onDownloadRequest: (Context.(
                url: String,
                fileName: String,
                mimeType: String?,
                contentLength: Long,
                downloadCallback: () -> Unit
        ) -> Dialog) = { url, fileName, _, contentLength, downloadCallback ->
            val msg = StringBuilder()
                    .append("文件名：$fileName").append("\n")
                    .append("\n")
                    .append("文件大小：$contentLength").append("\n")
                    .append("\n")
                    .append("下载地址：$url")
            AlertDialog.Builder(this)
                    .setTitle("是否下载？")
                    .setMessage(msg.toString())
                    .setPositiveButton("下载") { dialog, _ ->
                        downloadCallback.invoke()
                        dialog.dismiss()
                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
        },
        private val onDownloadExist: (Context.(id: Long) -> Unit) = {
            Toast.makeText(this, "下载已存在", Toast.LENGTH_SHORT).show()
        },
        private val onDownloadEnqueue: (Context.(id: Long) -> Unit) = {
            Toast.makeText(this, "开始下载", Toast.LENGTH_SHORT).show()
        }
) : AnoleAbility() {
    private var context: Context? = null
    private var downloadRequestDialog: Dialog? = null

    override fun onAttachToWebView(webView: AnoleView) {
        super.onAttachToWebView(webView)
        this.context = webView.context
    }

    override fun onDetachFromWebView(webView: AnoleView) {
        downloadRequestDialog?.cancel()
        this.context = null
        super.onDetachFromWebView(webView)
    }

    override fun onDownloadStart(
            url: String?,
            userAgent: String?,
            contentDisposition: String?,
            mimeType: String?,
            contentLength: Long
    ): Boolean {
        val context = context ?: return false
        val realUrl = getRealUrl(url) ?: return false
        downloadRequestDialog?.cancel()
        val fileName = getFilename(realUrl, contentDisposition, mimeType)
        downloadRequestDialog = onDownloadRequest.invoke(context, realUrl, fileName, mimeType, contentLength) {
            startDownload(context, realUrl, fileName, mimeType)
        }.apply {
            setOnDismissListener {
                downloadRequestDialog = null
            }
            show()
        }
        return true
    }

    private fun startDownload(
            context: Context,
            url: String,
            fileName: String,
            mimeType: String?
    ) {
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
        downloadManager ?: return
        val query = DownloadManager.Query()
        downloadManager.query(query)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val queryUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI))
                if (url == queryUrl) {
                    val queryId = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_ID))
                    onDownloadExist.invoke(context, queryId)
                    return
                }
            }
        }
        val request = DownloadManager.Request(Uri.parse(url))
        mimeType?.let { request.setMimeType(it) }
        request.setAllowedOverRoaming(allowedOverRoaming)
        var allowedNetworkTypes = DownloadManager.Request.NETWORK_WIFI
        if (allowedMobileNetwork) {
            allowedNetworkTypes = allowedNetworkTypes or DownloadManager.Request.NETWORK_MOBILE
        }
        request.setAllowedNetworkTypes(allowedNetworkTypes)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        request.setVisibleInDownloadsUi(true)
        val downloadId = downloadManager.enqueue(request)
        onDownloadEnqueue.invoke(context, downloadId)
    }

    private fun getRealUrl(url: String?): String? {
        url ?: return null
        var index = url.indexOf("https://")
        if (index >= 0) {
            return url.substring(index)
        }
        index = url.indexOf("http://")
        if (index >= 0) {
            return url.substring(index)
        }
        return null
    }

    private fun getFilename(
            url: String,
            contentDisposition: String?,
            mimeType: String?
    ): String {
        contentDisposition?.let { disposition ->
            val pattern = Pattern.compile("filename=\"(.*?)\"")
            val matcher = pattern.matcher(disposition)
            while (matcher.find()) {
                val fileName = matcher.group(1)
                if (!fileName.isNullOrBlank()) {
                    return fileName
                }
                break
            }
        }
        val ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)?.let {
            ".$it"
        } ?: ""
        Uri.parse(url).lastPathSegment?.let { fileName ->
            if (fileName.isNotBlank()) {
                return if (fileName.contains(".")) {
                    fileName
                } else {
                    "$fileName$ext"
                }
            }
        }
        try {
            val fileName = URLEncoder.encode(url, "GBK")
            if (!fileName.isNullOrBlank()) {
                return "$fileName$ext"
            }
        } catch (e: UnsupportedEncodingException) {
        }
        val fileName = UUID.randomUUID().toString()
        return "$fileName$ext"
    }
}