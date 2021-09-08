package per.goweii.anole.ability.impl

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import per.goweii.anole.Constants
import per.goweii.anole.ability.FileChooserParams
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.ValueCallback
import per.goweii.anole.kernel.WebKernel
import per.goweii.anole.utils.ResultUtils
import per.goweii.anole.utils.findActivity

class FileChooseAbility : WebAbility() {
    private val reqCode = Constants.REQUEST_CODE_CHOOSE_FILE

    private var activity: Activity? = null
    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    override fun onAttachToKernel(kernel: WebKernel) {
        super.onAttachToKernel(kernel)
        activity = kernel.kernelView.findActivity()
    }

    override fun onDetachFromKernel(kernel: WebKernel) {
        super.onDetachFromKernel(kernel)
        activity = null
        filePathCallback = null
    }

    override fun onShowFileChooser(
        webView: View,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: FileChooserParams?
    ): Boolean {
        val activity = activity ?: webView.findActivity()
        if (activity != null && filePathCallback != null && fileChooserParams != null) {
            if (chooseFiles(activity, fileChooserParams)) {
                this.filePathCallback = filePathCallback
                return true
            }
        }
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    private fun chooseFiles(
        activity: Activity,
        fileChooserParams: FileChooserParams
    ): Boolean {
        return try {
            val intent = createChooserIntent(fileChooserParams) ?: return false
            ResultUtils.startActivityResult(activity, intent, reqCode) { resultCode, data ->
                receiveChooseResult(fileChooserParams, resultCode, data)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun createChooserIntent(
        fileChooserParams: FileChooserParams
    ): Intent? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            fileChooserParams.createIntent()
        } else {
            Intent().apply {
                action = Intent.ACTION_GET_CONTENT
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
        }
    }

    private fun receiveChooseResult(
        fileChooserParams: FileChooserParams,
        resultCode: Int,
        data: Intent?
    ) {
        var uris: Array<Uri>? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            uris = fileChooserParams.parseResult(resultCode, data)
        }
        filePathCallback?.onReceiveValue(uris ?: emptyArray())
        filePathCallback = null
    }
}