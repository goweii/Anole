package per.goweii.anole

import android.content.Context
import per.goweii.anole.view.KernelView

interface WebInstanceBuilder<KV : KernelView> {
    fun build(context: Context): KV
}