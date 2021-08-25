package per.goweii.anole.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View

internal fun Context.findActivity(): Activity? {
    if (this is Activity) {
        return this
    }
    if (this is ContextWrapper) {
        val baseContext = this.baseContext
        if (baseContext is Activity) {
            return baseContext
        }
    }
    return null
}

internal fun View.findActivity(): Activity? {
    return context?.findActivity()
}