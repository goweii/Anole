package per.goweii.anole.ability.impl

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AlertDialog
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.ability.WebResourceRequest
import per.goweii.anole.kernel.WebKernel
import per.goweii.anole.utils.findActivity

class AppOpenAbility(
    private val onOpenApp: (Context.(
        uri: Uri,
        callback: (allow: Boolean) -> Unit
    ) -> Dialog) = { uri, callback ->
        AlertDialog.Builder(this)
            .setTitle("是否允许打开第三方APP？")
            .setMessage(uri.toString())
            .setPositiveButton("允许") { dialog, _ ->
                dialog.dismiss()
                callback.invoke(true)
            }
            .setNegativeButton("拒绝") { dialog, _ ->
                dialog.dismiss()
                callback.invoke(false)
            }
            .create()
    }
) : WebAbility() {
    private var mainHandler: Handler? = null
    private var openAppDialog: Dialog? = null

    override fun onAttachToKernel(kernel: WebKernel) {
        super.onAttachToKernel(kernel)
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onDetachFromKernel(kernel: WebKernel) {
        openAppDialog?.cancel()
        mainHandler?.removeCallbacksAndMessages(null)
        mainHandler = null
        super.onDetachFromKernel(kernel)
    }

    override fun shouldOverrideUrlLoading(
        webView: View,
        request: WebResourceRequest
    ): Boolean {
        val scheme = request.uri.scheme
        if (!("http" == scheme || "https" == scheme)) {
            val activity = activity ?: webView.findActivity()
            activity?.let {
                mainHandler?.post {
                    showOpenAppDialog(activity, request.uri)
                }
            }
            return true
        }
        return super.shouldOverrideUrlLoading(webView, request)
    }

    private fun showOpenAppDialog(activity: Activity, reqUri: Uri) {
        onOpenApp.invoke(activity, reqUri) {
            if (it) {
                openApp(activity, reqUri)
            }
        }
    }

    private fun openApp(activity: Activity, reqUri: Uri) {
        try {
            activity.startActivity(Intent(Intent.ACTION_VIEW, reqUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            })
        } catch (e: Exception) {
        }
    }
}