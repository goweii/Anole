package per.goweii.anole.ability.impl

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import per.goweii.anole.Constants
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.utils.ResultUtils
import per.goweii.anole.utils.findActivity

class FileChooseAbility(
    private val activity: Activity? = null
) : WebAbility() {
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null

    private val reqCode = Constants.REQUEST_CODE_CHOOSE_FILE

    override fun onShowFileChooser(
        webView: View,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        val activity = activity ?: webView.findActivity()
        if (activity != null && filePathCallback != null && fileChooserParams != null) {
            if (chooseFiles(activity, fileChooserParams)) {
                mFilePathCallback = filePathCallback
                return true
            }
        }
        return super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
    }

    private fun chooseFiles(
        activity: Activity,
        fileChooserParams: WebChromeClient.FileChooserParams
    ): Boolean {
        return try {
            val intent = createChooserIntent(fileChooserParams)
            ResultUtils.startActivityResult(activity, intent, reqCode) { resultCode, data ->
                receiveChooseResult(resultCode, data)
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun createChooserIntent(
        fileChooserParams: WebChromeClient.FileChooserParams
    ): Intent {
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

    private fun receiveChooseResult(resultCode: Int, data: Intent?) {
        var uris: Array<Uri>? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            uris = WebChromeClient.FileChooserParams.parseResult(resultCode, data)
        }
        mFilePathCallback?.onReceiveValue(uris ?: emptyArray())
        mFilePathCallback = null
    }
}