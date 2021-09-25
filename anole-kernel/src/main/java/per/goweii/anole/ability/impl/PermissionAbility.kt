package per.goweii.anole.ability.impl

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import per.goweii.anole.Constants
import per.goweii.anole.R
import per.goweii.anole.ability.GeolocationPermissions
import per.goweii.anole.ability.PermissionRequest
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.WebKernel
import per.goweii.anole.utils.ResultUtils

class PermissionAbility(
    private val requestPermissionsProxy: RequestPermissionsProxy = object :
        RequestPermissionsProxy() {
        private var dialog: Dialog? = null

        override fun show(
            activity: Activity,
            kernel: WebKernel,
            origin: Uri,
            permissions: Array<String>
        ) {
            dialog?.dismiss()
            val permissionSysList = arrayListOf<String>()
            val permissionResList = arrayListOf<String>()
            val permissionNameList = arrayListOf<String>()
            permissions.forEach { permission ->
                when (permission) {
                    PermissionRequest.RESOURCE_VIDEO_CAPTURE -> {
                        permissionSysList.add(Manifest.permission.CAMERA)
                        permissionResList.add(permission)
                        permissionNameList.add(activity.getString(R.string.anole_resource_video_capture))
                    }
                    PermissionRequest.RESOURCE_AUDIO_CAPTURE -> {
                        permissionSysList.add(Manifest.permission.RECORD_AUDIO)
                        permissionResList.add(permission)
                        permissionNameList.add(activity.getString(R.string.anole_resource_audio_capture))
                    }
                    PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            permissionSysList.add(Manifest.permission.MEDIA_CONTENT_CONTROL)
                        }
                        permissionResList.add(permission)
                        permissionNameList.add(activity.getString(R.string.anole_resource_protected_media_id))
                    }
                    PermissionRequest.RESOURCE_MIDI_SYSEX -> {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            permissionSysList.add(Manifest.permission.BIND_MIDI_DEVICE_SERVICE)
                        }
                        permissionResList.add(permission)
                        permissionNameList.add(activity.getString(R.string.anole_resource_midi_sysex))
                    }
                }
            }
            val permissionCheckedList = permissionNameList.map { true }.toMutableList()
            dialog = AlertDialog.Builder(activity)
                .setTitle("是否允许网页获取以下权限？")
                .setMultiChoiceItems(
                    permissionNameList.toTypedArray(),
                    permissionCheckedList.toBooleanArray()
                ) { _, which, isChecked ->
                    permissionCheckedList[which] = isChecked
                }
                .setPositiveButton("允许") { dialog, _ ->
                    dialog.dismiss()
                    ResultUtils.requestPermissionsResult(
                        activity,
                        permissionSysList.filterIndexed { index, _ ->
                            permissionCheckedList[index]
                        }.toTypedArray(),
                        Constants.REQUEST_CODE_PERMISSIONS
                    ) { permissions, grantResults ->
                        grant(permissionResList.filterIndexed { index, _ ->
                            grantResults.getOrNull(index) == PackageManager.PERMISSION_GRANTED
                        }.toTypedArray())
                    }
                }
                .setNeutralButton("全部允许") { dialog, _ ->
                    dialog.dismiss()
                    ResultUtils.requestPermissionsResult(
                        activity,
                        permissionSysList.toTypedArray(),
                        Constants.REQUEST_CODE_PERMISSIONS
                    ) { permissions, grantResults ->
                        grant(permissionResList.filterIndexed { index, _ ->
                            grantResults.getOrNull(index) == PackageManager.PERMISSION_GRANTED
                        }.toTypedArray())
                    }
                }
                .setNegativeButton("拒绝") { dialog, _ ->
                    dialog.dismiss()
                    deny()
                }
                .create()
            dialog?.setOnDismissListener {
                dialog = null
            }
            dialog?.show()
        }

        override fun cancel() {
            dialog?.cancel()
            dialog = null
            grant(emptyArray())
        }
    },
    private val geolocationPermissionsProxy: GeolocationPermissionsProxy = object :
        GeolocationPermissionsProxy() {
        private var dialog: Dialog? = null

        override fun show(activity: Activity, origin: String) {
            dialog?.dismiss()
            var retain = false
            dialog = AlertDialog.Builder(activity)
                .setTitle("是否允许网页获取定位权限？")
                .setMultiChoiceItems(
                    arrayOf("记住选择"),
                    booleanArrayOf(retain)
                ) { _, _, isChecked ->
                    retain = isChecked
                }
                .setPositiveButton("允许") { dialog, _ ->
                    dialog.dismiss()
                    allow(retain)
                }
                .setNegativeButton("拒绝") { dialog, _ ->
                    dialog.dismiss()
                    refuse(retain)
                }
                .create()
            dialog?.setOnDismissListener {
                dialog = null
            }
            dialog?.show()
        }

        override fun cancel() {
            dialog?.cancel()
            dialog = null
            refuse(false)
        }
    }
) : WebAbility() {
    private var mainHandler: Handler? = null

    override fun onAttachToKernel(kernel: WebKernel) {
        super.onAttachToKernel(kernel)
        mainHandler = Handler(Looper.getMainLooper())
    }

    override fun onDetachFromKernel(kernel: WebKernel) {
        requestPermissionsProxy.cancel()
        geolocationPermissionsProxy.cancel()
        mainHandler?.removeCallbacksAndMessages(null)
        mainHandler = null
        super.onDetachFromKernel(kernel)
    }

    override fun onPermissionRequest(request: PermissionRequest?): Boolean {
        val mainHandler = mainHandler ?: return super.onPermissionRequest(request)
        val activity = activity ?: return super.onPermissionRequest(request)
        val kernel = kernel ?: return super.onPermissionRequest(request)
        request ?: return super.onPermissionRequest(request)
        mainHandler.post { showPermissionRequestDialog(activity, kernel, request) }
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
        val activity = activity ?: return super.onGeolocationPermissionsShowPrompt(origin, callback)
        origin ?: return super.onGeolocationPermissionsShowPrompt(origin, callback)
        callback ?: return super.onGeolocationPermissionsShowPrompt(origin, callback)
        mainHandler.post { showGeolocationPermissionsDialog(activity, origin, callback) }
        return true
    }

    override fun onGeolocationPermissionsHidePrompt(): Boolean {
        val mainHandler = mainHandler ?: return super.onGeolocationPermissionsHidePrompt()
        mainHandler.post { hideGeolocationPermissionsDialog() }
        return true
    }

    private fun showPermissionRequestDialog(
        activity: Activity,
        kernel: WebKernel,
        request: PermissionRequest
    ) {
        requestPermissionsProxy.onGrant = { request.grant(it) }
        requestPermissionsProxy.onDeny = { request.deny() }
        requestPermissionsProxy.show(activity, kernel, request.origin, request.resources)
    }

    private fun cancelPermissionRequestDialog() {
        requestPermissionsProxy.cancel()
        requestPermissionsProxy.onGrant = null
        requestPermissionsProxy.onDeny = null
    }

    private fun showGeolocationPermissionsDialog(
        activity: Activity,
        origin: String,
        callback: GeolocationPermissions.Callback
    ) {
        geolocationPermissionsProxy.onAllow = { callback.invoke(origin, true, it) }
        geolocationPermissionsProxy.onRefuse = { callback.invoke(origin, false, it) }
        geolocationPermissionsProxy.show(activity, origin)
    }

    private fun hideGeolocationPermissionsDialog() {
        geolocationPermissionsProxy.cancel()
        geolocationPermissionsProxy.onAllow = null
        geolocationPermissionsProxy.onRefuse = null
    }

    abstract class RequestPermissionsProxy {
        var onGrant: ((granted: Array<String>) -> Unit)? = null
        var onDeny: (() -> Unit)? = null

        abstract fun show(
            activity: Activity,
            kernel: WebKernel,
            origin: Uri,
            permissions: Array<String>
        )

        abstract fun cancel()

        fun grant(granted: Array<String>) {
            onGrant?.invoke(granted)
        }

        fun deny() {
            onDeny?.invoke()
        }
    }

    abstract class GeolocationPermissionsProxy {
        var onAllow: ((retain: Boolean) -> Unit)? = null
        var onRefuse: ((retain: Boolean) -> Unit)? = null

        abstract fun show(activity: Activity, origin: String)

        abstract fun cancel()

        fun allow(retain: Boolean) {
            onAllow?.invoke(retain)
        }

        fun refuse(retain: Boolean) {
            onRefuse?.invoke(retain)
        }
    }
}