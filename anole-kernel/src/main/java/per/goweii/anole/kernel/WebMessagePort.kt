package per.goweii.anole.kernel

import android.os.Handler

abstract class WebMessagePort {
    abstract class WebMessageCallback {
        fun onMessage(port: WebMessagePort?, message: WebMessage?) {}
    }

    abstract fun postMessage(message: WebMessage?)
    abstract fun close()
    abstract fun setWebMessageCallback(callback: WebMessageCallback?)
    abstract fun setWebMessageCallback(callback: WebMessageCallback?, handler: Handler?)
}