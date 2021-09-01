package per.goweii.android.anole

import android.app.Application
import android.content.Context
import per.goweii.anole.WebFactory
import per.goweii.anole.kernel.system.SystemWebInstanceBuilder

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
        WebFactory.setInstanceBuilder(SystemWebInstanceBuilder())
    }
}