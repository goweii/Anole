package per.goweii.anole.kernel

import androidx.annotation.UiThread

interface VisualStateCallback {
    @UiThread
    fun onComplete(requestId: Long)
}