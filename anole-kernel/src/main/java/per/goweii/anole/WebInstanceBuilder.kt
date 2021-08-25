package per.goweii.anole

import android.content.Context
import per.goweii.anole.view.KernelView

interface WebInstanceBuilder {
    fun build(context: Context): KernelView
}