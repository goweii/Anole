package per.goweii.anole.ability

import java.io.InputStream

data class WebResourceResponse(
    val mimeType: String? = null,
    val encoding: String? = null,
    val statusCode: Int = -1,
    val reasonPhrase: String? = null,
    val inputStream: InputStream? = null,
    val responseHeaders: Map<String, String>? = null
)