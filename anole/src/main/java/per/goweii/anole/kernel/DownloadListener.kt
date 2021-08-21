package per.goweii.anole.kernel

interface DownloadListener {
    fun onDownloadStart(
        url: String?, userAgent: String?,
        contentDisposition: String?, mimetype: String?, contentLength: Long
    )
}