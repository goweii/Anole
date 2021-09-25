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
import per.goweii.anole.R
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.ability.WebResourceRequest
import per.goweii.anole.kernel.WebKernel

class AppOpenAbility(
    private val onOpenApp: (Context.(
        uri: Uri,
        callback: () -> Unit
    ) -> Dialog) = { uri, callback ->
        AlertDialog.Builder(this)
            .setTitle(R.string.anole_open_other_app)
            .setMessage(uri.toString())
            .setPositiveButton(R.string.anole_allow) { dialog, _ ->
                dialog.dismiss()
                callback.invoke()
            }
            .setNegativeButton(R.string.anole_deny) { dialog, _ ->
                dialog.dismiss()
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
            activity?.let {
                mainHandler?.post {
                    showOpenAppDialog(it, request.uri)
                }
            }
            return true
        }
        return super.shouldOverrideUrlLoading(webView, request)
    }

    private fun showOpenAppDialog(activity: Activity, reqUri: Uri) {
        openAppDialog = onOpenApp.invoke(activity, reqUri) {
            openApp(activity, reqUri)
        }
        openAppDialog?.show()
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