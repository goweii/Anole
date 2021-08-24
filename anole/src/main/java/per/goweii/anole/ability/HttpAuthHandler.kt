package per.goweii.anole.ability

import android.os.Handler
import android.os.Looper

abstract class HttpAuthHandler(looper: Looper) : Handler(looper) {
    abstract fun useHttpAuthUsernamePassword(): Boolean
    abstract fun cancel()
    abstract fun proceed(username: String?, password: String?)
}