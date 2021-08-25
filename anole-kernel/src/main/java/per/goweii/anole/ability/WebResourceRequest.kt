package per.goweii.anole.ability

import android.net.Uri

data class WebResourceRequest(
    val uri: Uri,
    val isForMainFrame: Boolean = false,
    val isRedirect: Boolean = false,
    val hasGesture: Boolean = false,
    val method: String? = null,
    val requestHeaders: Map<String, String>? = null
)