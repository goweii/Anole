package per.goweii.anole.ability.impl

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.fragment.app.Fragment
import per.goweii.anole.ability.AnoleAbility

class FileChooseAbility(
    private val activity: Activity? = null
) : AnoleAbility() {
    private var mFilePathCallback: ValueCallback<Array<Uri>>? = null

    private val REQ_CODE = 1001

    override fun onShowFileChooser(
        webView: WebView,
        filePathCallback: ValueCallback<Array<Uri>>?,
        fileChooserParams: WebChromeClient.FileChooserParams?
    ): Boolean {
        val activity = activity ?: findActivityFromContext(webView.context)
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
            ResultApi.startActivityResult(
                    activity,
                    intent,
                    REQ_CODE
            ) { resultCode, data ->
                chooseResult(resultCode, data)
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

    private fun chooseResult(resultCode: Int, data: Intent?) {
        var uris: Array<Uri>? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            uris = WebChromeClient.FileChooserParams.parseResult(resultCode, data)
        }
        mFilePathCallback?.onReceiveValue(uris ?: emptyArray())
        mFilePathCallback = null
    }

    private fun findActivityFromContext(context: Context): Activity? {
        var activity: Activity? = null
        if (context is Activity) {
            activity = context
        } else {
            if (context is ContextWrapper) {
                val baseContext = context.baseContext
                if (baseContext is Activity) {
                    activity = baseContext
                }
            }
        }
        return activity
    }

    class FileChooserFragmentX : Fragment() {
        internal var onFileChooserResult: ((
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        ) -> Unit)? = null

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            onFileChooserResult?.invoke(requestCode, resultCode, data)
        }
    }

    class FileChooserFragment : android.app.Fragment() {
        internal var onFileChooserResult: ((
            requestCode: Int,
            resultCode: Int,
            data: Intent?
        ) -> Unit)? = null

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            onFileChooserResult?.invoke(requestCode, resultCode, data)
        }
    }
}