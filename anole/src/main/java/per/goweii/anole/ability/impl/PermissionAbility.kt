package per.goweii.anole.ability.impl

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import per.goweii.anole.R
import per.goweii.anole.ability.AnoleAbility
import per.goweii.anole.view.AnoleView

class PermissionAbility(
        private val onRequestPermissions: (Context.(
                origin: Uri?,
                permissions: Array<String>,
                callback: (granted: Array<String>?) -> Unit
        ) -> Dialog) = { _, permissions, callback ->
            val permissionResList = arrayListOf<String>()
            val permissionNameList = arrayListOf<String>()
            permissions.forEach { permission ->
                when (permission) {
                    PermissionRequest.RESOURCE_VIDEO_CAPTURE -> {
                        permissionResList.add(permission)
                        permissionNameList.add(this.getString(R.string.anole_resource_video_capture))
                    }
                    PermissionRequest.RESOURCE_AUDIO_CAPTURE -> {
                        permissionResList.add(permission)
                        permissionNameList.add(this.getString(R.string.anole_resource_audio_capture))
                    }
                    PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID -> {
                        permissionResList.add(permission)
                        permissionNameList.add(this.getString(R.string.anole_resource_protected_media_id))
                    }
                    PermissionRequest.RESOURCE_MIDI_SYSEX -> {
                        permissionResList.add(permission)
                        permissionNameList.add(this.getString(R.string.anole_resource_midi_sysex))
                    }
                }
            }
            val permissionCheckedList = permissionNameList.map { true }.toMutableList()
            AlertDialog.Builder(this)
                    .setTitle("是否允许网页获取以下权限？")
                    .setMultiChoiceItems(permissionNameList.toTypedArray(), permissionCheckedList.toBooleanArray()) { _, which, isChecked ->
                        permissionCheckedList[which] = isChecked
                    }
                    .setPositiveButton("允许") { dialog, _ ->
                        dialog.dismiss()
                        callback.invoke(permissionResList.filterIndexed { index, _ ->
                            permissionCheckedList[index]
                        }.toTypedArray())
                    }
                    .setNeutralButton("全部允许") { dialog, _ ->
                        dialog.dismiss()
                        callback.invoke(permissionResList.toTypedArray())
                    }
                    .setNegativeButton("拒绝") { dialog, _ ->
                        dialog.dismiss()
                        callback.invoke(null)
                    }
                    .create()
        },
        private val onRequestGeolocationPermissions: (Context.(
                origin: String,
                callback: (allow: Boolean, retain: Boolean) -> Unit
        ) -> Dialog) = { _, callback ->
            AlertDialog.Builder(this)
                    .setTitle("是否允许网页获取定位权限？")
                    .setPositiveButton("本次允许") { dialog, _ ->
                        dialog.dismiss()
                        callback.invoke(true, false)
                    }
                    .setNeutralButton("永久允许") { dialog, _ ->
                        dialog.dismiss()
                        callback.invoke(true, true)
                    }
                    .setNegativeButton("拒绝") { dialog, _ ->
                        dialog.dismiss()
                        callback.invoke(false, false)
                    }
                    .create()
        }
) : AnoleAbility() {
    private var mainHandler: Handler? = null
    private var context: Context? = null
    private var permissionRequestDialog: Dialog? = null
    private var geolocationPermissionsDialog: Dialog? = null

    override fun onAttachToWebView(webView: AnoleView) {
        super.onAttachToWebView(webView)
        context = webView.context
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onDetachFromWebView(webView: AnoleView) {
        permissionRequestDialog?.cancel()
        geolocationPermissionsDialog?.cancel()
        mainHandler?.removeCallbacksAndMessages(null)
        mainHandler = null
        context = null
        super.onDetachFromWebView(webView)
    }

    override fun onPermissionRequest(request: PermissionRequest?): Boolean {
        val mainHandler = mainHandler ?: return super.onPermissionRequest(request)
        val context = context ?: return super.onPermissionRequest(request)
        request ?: return super.onPermissionRequest(request)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return super.onPermissionRequest(request)
        }
        mainHandler.post { showPermissionRequestDialog(context, request) }
        return true
    }

    override fun onPermissionRequestCanceled(request: PermissionRequest?): Boolean {
        val mainHandler = mainHandler ?: return super.onPermissionRequest(request)
        request ?: return super.onPermissionRequestCanceled(request)
        mainHandler.post { cancelPermissionRequestDialog() }
        return true
    }

    override fun onGeolocationPermissionsShowPrompt(
            origin: String?,
            callback: GeolocationPermissions.Callback?
    ): Boolean {
        val mainHandler = mainHandler
                ?: return super.onGeolocationPermissionsShowPrompt(origin, callback)
        val context = context ?: return super.onGeolocationPermissionsShowPrompt(origin, callback)
        origin ?: return super.onGeolocationPermissionsShowPrompt(origin, callback)
        callback ?: return super.onGeolocationPermissionsShowPrompt(origin, callback)
        mainHandler.post { showGeolocationPermissionsDialog(context, origin, callback) }
        return true
    }

    override fun onGeolocationPermissionsHidePrompt(): Boolean {
        val mainHandler = mainHandler ?: return super.onGeolocationPermissionsHidePrompt()
        mainHandler.post { hideGeolocationPermissionsDialog() }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun showPermissionRequestDialog(context: Context, request: PermissionRequest) {
        permissionRequestDialog?.dismiss()
        permissionRequestDialog = onRequestPermissions(context, request.origin, request.resources) { permissions ->
            permissions?.let { request.grant(it) } ?: request.deny()
        }.apply {
            setOnDismissListener {
                permissionRequestDialog = null
            }
            show()
        }
    }

    private fun cancelPermissionRequestDialog() {
        permissionRequestDialog?.cancel()
    }

    private fun showGeolocationPermissionsDialog(context: Context, origin: String, callback: GeolocationPermissions.Callback) {
        geolocationPermissionsDialog?.dismiss()
        geolocationPermissionsDialog = onRequestGeolocationPermissions(context, origin) { allow, retain ->
            callback.invoke(origin, allow, retain)
        }.apply {
            setOnDismissListener {
                geolocationPermissionsDialog = null
            }
            show()
        }
    }

    private fun hideGeolocationPermissionsDialog() {
        geolocationPermissionsDialog?.cancel()
    }
}