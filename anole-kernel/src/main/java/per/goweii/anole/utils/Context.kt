package per.goweii.anole.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View

internal fun Context.findActivity(): Activity? {
    var context: Context = this
    while (true) {
        if (context is Activity) return context
        if (this !is ContextWrapper) return null
        if (baseContext === context) return null
        context = baseContext
    }
}

internal fun View.findActivity(): Activity? {
    context?.findActivity()?.let { return it }
    if (rootView != this) {
        return rootView.context?.findActivity()
    }
    return null
}