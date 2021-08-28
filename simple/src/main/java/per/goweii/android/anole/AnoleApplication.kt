package per.goweii.android.anole

import android.app.Application
import android.content.Context
import per.goweii.anole.WebFactory
import per.goweii.anole.view.KernelView

class AnoleApplication : Application() {
    companion object {
        lateinit var application: AnoleApplication
    }

    override fun attachBaseContext(base: Context?) {
        application = this
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()
        WebFactory.setInstanceBuilder(object :
            per.goweii.anole.kernel.system.SystemWebInstanceBuilder() {
            override fun build(context: Context): KernelView {
                return super.build(context).also {
                    //val progressBar = LayoutInflater.from(context)
                    //    .inflate(R.layout.progress_bar, it, false) as ProgressBar
                    //it.addView(progressBar)
                    //it.webClient.addAbility(ProgressAbility(progressBar))
                }
            }
        })
    }
}