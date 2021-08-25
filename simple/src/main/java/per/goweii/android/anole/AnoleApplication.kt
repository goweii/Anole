package per.goweii.android.anole

import android.app.Application
import android.content.Context
import android.view.LayoutInflater
import android.widget.ProgressBar
import per.goweii.anole.WebFactory
import per.goweii.anole.ability.impl.ProgressAbility
import per.goweii.anole.view.KernelView

class AnoleApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        WebFactory.setInstanceBuilder(object :
            per.goweii.anole.kernel.system.SystemWebInstanceBuilder() {
            override fun build(context: Context): KernelView {
                return super.build(context).also {
                    val progressBar = LayoutInflater.from(context)
                        .inflate(R.layout.progress_bar, it, false) as ProgressBar
                    it.addView(progressBar)
                    it.webClient.addAbility(ProgressAbility(progressBar))
                }
            }
        })
    }
}