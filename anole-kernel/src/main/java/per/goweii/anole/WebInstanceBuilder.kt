package per.goweii.anole

import android.content.Context
import per.goweii.anole.kernel.WebKernel

interface WebInstanceBuilder {
    fun build(context: Context, parent: WebKernel?): WebKernel
}