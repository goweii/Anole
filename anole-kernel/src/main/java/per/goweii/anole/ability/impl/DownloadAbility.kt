package per.goweii.anole.ability.impl

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import per.goweii.anole.Constants
import per.goweii.anole.R
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.WebKernel
import per.goweii.anole.utils.FormatUtils
import per.goweii.anole.utils.ResultUtils
import per.goweii.anole.utils.findActivity
import java.util.*
import java.util.regex.Pattern

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
            .append(getString(R.string.anole_download_filename, fileName))
            .append("\n")
            .append("\n")
            .append(
                getString(
                    R.string.anole_download_content_length,
                    FormatUtils.formatContentLength(contentLength)
                )
            )
            .append("\n")
            .append("\n")
            .append(getString(R.string.anole_download_url, url))
        AlertDialog.Builder(this)
            .setTitle(R.string.anole_download_title)
            .setMessage(msg.toString())
            .setPositiveButton(R.string.anole_download) { dialog, _ ->
                dialog.dismiss()
                findActivity()?.let { activity ->
                    ResultUtils.requestPermissionsResult(
                        activity,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        Constants.REQUEST_CODE_PERMISSION_DOWNLOAD
                    ) { _, grantResults ->
                        if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                            downloadCallback.invoke()
                        }
                    }
                }
            }
            .setNegativeButton(R.string.anole_cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    },
    private val onDownloadExist: (Context.(id: Long) -> Unit) = {
        Toast.makeText(this, getString(R.string.anole_download_exist), Toast.LENGTH_SHORT).show()
    },
    private val onDownloadEnqueue: (Context.(id: Long) -> Unit) = {
        Toast.makeText(this, getString(R.string.anole_download_start), Toast.LENGTH_SHORT).show()
    }
) : WebAbility() {
    private var downloadRequestDialog: Dialog? = null

    override fun onDetachFromKernel(kernel: WebKernel) {
        downloadRequestDialog?.cancel()
        super.onDetachFromKernel(kernel)
    }

    override fun onDownloadStart(
        url: String?,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?,
        contentLength: Long
    ): Boolean {
        val context = activity ?: return false
        val realUrl = getRealUrl(url) ?: return false
        downloadRequestDialog?.cancel()
        val fileName = getFilename(realUrl, contentDisposition, mimeType)
        downloadRequestDialog =
            onDownloadRequest.invoke(context, realUrl, fileName, mimeType, contentLength) {
                startDownload(context, realUrl, fileName, mimeType)
            }.apply {
                setOnDismissListener {
                    downloadRequestDialog = null
                }
                show()
            }
        return true
    }

    @SuppressLint("Range")
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
        var filename: String? = null
        var extension: String? = null
        contentDisposition?.let { disposition ->
            val pattern = Pattern.compile("filename=\"(.*?)\"")
            val matcher = pattern.matcher(disposition)
            while (matcher.find()) {
                val fileName = matcher.group(1)
                if (!fileName.isNullOrBlank()) {
                    filename = fileName
                }
                break
            }
        }
        if (filename.isNullOrBlank()) {
            Uri.parse(url).lastPathSegment?.let { fileName ->
                if (fileName.isNotBlank()) {
                    filename = fileName
                }
            }
        }
        if (!filename.isNullOrBlank()) {
            val dotIndex = filename!!.lastIndexOf(".")
            if (dotIndex > 0) {
                val maybeExt = filename!!.substring(dotIndex + 1)
                val typeFromExt = MimeTypeMap.getSingleton().getMimeTypeFromExtension(maybeExt)
                if (!typeFromExt.isNullOrBlank()) {
                    filename = filename!!.substring(0, dotIndex)
                    extension = maybeExt
                }
            }
        }
        if (filename.isNullOrBlank()) {
            val prefix = activity?.getString(R.string.anole_download_filename_prefix)
            filename = if (prefix.isNullOrBlank()) {
                "${System.currentTimeMillis()}"
            } else {
                "${prefix}_${System.currentTimeMillis()}"
            }
        }
        if (!mimeType.isNullOrBlank()) {
            val extFromType = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)
            if (!extFromType.isNullOrBlank()) {
                extension = extFromType
            }
            if (extension.isNullOrBlank()) {
                if (mimeType.lowercase(Locale.ROOT).startsWith("text/")) {
                    extension = if (mimeType.equals("text/html", ignoreCase = true)) {
                        ".html"
                    } else {
                        ".txt"
                    }
                }
            }
            if (extension.isNullOrBlank()) {
                val index = mimeType.lastIndexOf("/")
                if (index > 0) {
                    val maybeExt = mimeType.substring(index + 1)
                    val maybeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(maybeExt)
                    if (!maybeType.isNullOrBlank()) {
                        val extFromMaybeType =
                            MimeTypeMap.getSingleton().getExtensionFromMimeType(maybeType)
                        if (!extFromMaybeType.isNullOrBlank()) {
                            extension = extFromMaybeType
                        }
                    }
                }
            }
        }
        return if (extension.isNullOrBlank()) {
            "$filename"
        } else {
            "$filename.$extension"
        }
    }
}