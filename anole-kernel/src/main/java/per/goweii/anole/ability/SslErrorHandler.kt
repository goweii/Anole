package per.goweii.anole.ability

import android.os.Handler
import android.os.Looper

abstract class SslErrorHandler(looper: Looper) : Handler(looper) {
    abstract fun proceed()
    abstract fun cancel()
}