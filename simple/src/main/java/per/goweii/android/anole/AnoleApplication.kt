package per.goweii.android.anole

import android.app.Application
import android.content.Context

class AnoleApplication : Application() {
    companion object {
        lateinit var application: AnoleApplication
    }

    override fun attachBaseContext(base: Context?) {
        application = this
        super.attachBaseContext(base)
    }

}