package per.goweii.anole.ability.impl

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import per.goweii.anole.ability.WebAbility

class AppOpenAbility : WebAbility() {
    override fun shouldOverrideUrlLoading(
        view: WebView,
        reqUri: Uri,
        reqHeaders: Map<String, String>?,
        reqMethod: String?,
        userAgent: String?
    ): Boolean {
        val scheme = reqUri.scheme
        if (!("http" == scheme || "https" == scheme)) {
            try {
                view.context.startActivity(Intent(Intent.ACTION_VIEW, reqUri).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                })
            } catch (e: Exception) {
            }
            return true
        }
        return super.shouldOverrideUrlLoading(view, reqUri, reqHeaders, reqMethod, userAgent)
    }
}