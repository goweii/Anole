package per.goweii.android.anole.utils.ext

import android.view.View
import android.view.ViewGroup

fun View.removeFromParent() {
    val parent = parent ?: return
    if (parent is ViewGroup) {
        parent.removeView(this)
    }
}