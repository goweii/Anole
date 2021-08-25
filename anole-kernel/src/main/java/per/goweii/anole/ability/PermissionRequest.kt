package per.goweii.anole.ability

import android.net.Uri

abstract class PermissionRequest(
    val origin: Uri,
    val resources: Array<String>
) {
    abstract fun grant(resources: Array<String>)
    abstract fun deny()

    companion object {
        const val RESOURCE_VIDEO_CAPTURE = "android.webkit.resource.VIDEO_CAPTURE"
        const val RESOURCE_AUDIO_CAPTURE = "android.webkit.resource.AUDIO_CAPTURE"
        const val RESOURCE_PROTECTED_MEDIA_ID = "android.webkit.resource.PROTECTED_MEDIA_ID"
        const val RESOURCE_MIDI_SYSEX = "android.webkit.resource.MIDI_SYSEX"
    }
}