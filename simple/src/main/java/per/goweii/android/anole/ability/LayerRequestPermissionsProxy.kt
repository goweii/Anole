package per.goweii.android.anole.ability

import android.Manifest
import android.app.Activity
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import androidx.core.view.isVisible
import per.goweii.android.anole.databinding.LayerRequestPermissionsBinding
import per.goweii.anole.Constants
import per.goweii.anole.ability.PermissionRequest
import per.goweii.anole.ability.impl.PermissionAbility
import per.goweii.anole.kernel.WebKernel
import per.goweii.anole.utils.ResultUtils
import per.goweii.anole.view.KernelView
import per.goweii.layer.core.Layer

class LayerRequestPermissionsProxy : PermissionAbility.RequestPermissionsProxy() {
    private var dialog: KernelViewLayer? = null
    private var responsed = false

    override fun show(
        activity: Activity,
        kernel: WebKernel,
        origin: Uri,
        permissions: Array<String>
    ) {
        dialog?.dismiss()
        val kernelView = kernel.kernelView as KernelView
        val binding = LayerRequestPermissionsBinding.inflate(
            LayoutInflater.from(activity), kernelView, false
        )
        dialog = KernelViewLayer(kernelView, binding.root).apply {
            addOnVisibleChangeListener(object : Layer.OnVisibleChangedListener {
                override fun onShow(layer: Layer) {
                    responsed = false
                }

                override fun onDismiss(layer: Layer) {
                    dialog = null
                    if (!responsed) {
                        responsed = true
                        deny()
                    }
                }
            })
            permissions.forEach { permission ->
                when (permission) {
                    PermissionRequest.RESOURCE_VIDEO_CAPTURE -> {
                        binding.layerRequestPermissionsCbResourceVideoCapture.isVisible = true
                    }
                    PermissionRequest.RESOURCE_AUDIO_CAPTURE -> {
                        binding.layerRequestPermissionsCbResourceAudioCapture.isVisible = true
                    }
                    PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID -> {
                        binding.layerRequestPermissionsCbResourceProtectedMediaId.isVisible = true
                    }
                    PermissionRequest.RESOURCE_MIDI_SYSEX -> {
                        binding.layerRequestPermissionsCbResourceMidiSysex.isVisible = true
                    }
                }
            }
            fun getCheckedPermissions(all: Boolean): Map<String, String> {
                val checkedPermissions = mutableMapOf<String, String>()
                permissions.forEach { permission ->
                    when (permission) {
                        PermissionRequest.RESOURCE_VIDEO_CAPTURE -> {
                            if (all || binding.layerRequestPermissionsCbResourceVideoCapture.isChecked) {
                                checkedPermissions[permission] = Manifest.permission.CAMERA
                            }
                        }
                        PermissionRequest.RESOURCE_AUDIO_CAPTURE -> {
                            if (all || binding.layerRequestPermissionsCbResourceAudioCapture.isChecked) {
                                checkedPermissions[permission] = Manifest.permission.RECORD_AUDIO
                            }
                        }
                        PermissionRequest.RESOURCE_PROTECTED_MEDIA_ID -> {
                            if (all || binding.layerRequestPermissionsCbResourceProtectedMediaId.isChecked) {
                                checkedPermissions[permission] =
                                    Manifest.permission.MEDIA_CONTENT_CONTROL
                            }
                        }
                        PermissionRequest.RESOURCE_MIDI_SYSEX -> {
                            if (all || binding.layerRequestPermissionsCbResourceMidiSysex.isChecked) {
                                checkedPermissions[permission] =
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        Manifest.permission.BIND_MIDI_DEVICE_SERVICE
                                    } else {
                                        ""
                                    }
                            }
                        }
                    }
                }
                return checkedPermissions
            }
            binding.layerRequestPermissionsTvGrant.setOnClickListener {
                val checkedPermissions = getCheckedPermissions(false)
                if (checkedPermissions.isEmpty()) {
                    return@setOnClickListener
                }
                responsed = true
                dismiss()
                ResultUtils.requestPermissionsResult(
                    activity,
                    checkedPermissions.map { it.value }.filter { it.isNotBlank() }.toTypedArray(),
                    Constants.REQUEST_CODE_PERMISSIONS
                ) { _, _ ->
                    grant(checkedPermissions.map { it.key }.toTypedArray())
                }
            }
            binding.layerRequestPermissionsTvGrantAll.setOnClickListener {
                responsed = true
                dismiss()
                val checkedPermissions = getCheckedPermissions(true)
                ResultUtils.requestPermissionsResult(
                    activity,
                    checkedPermissions.map { it.value }.filter { it.isNotBlank() }.toTypedArray(),
                    Constants.REQUEST_CODE_PERMISSIONS
                ) { _, _ ->
                    grant(checkedPermissions.map { it.key }.toTypedArray())
                }
            }
            binding.layerRequestPermissionsTvDeny.setOnClickListener {
                responsed = true
                dismiss()
                deny()
            }
        }
        dialog?.show()
    }

    override fun cancel() {
        dialog?.dismiss()
    }
}