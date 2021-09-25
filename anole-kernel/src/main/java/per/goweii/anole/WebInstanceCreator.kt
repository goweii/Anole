package per.goweii.anole

import android.content.Context
import per.goweii.anole.kernel.WebKernel

interface WebInstanceCreator {
    fun build(context: Context): WebKernel
}