package per.goweii.anole.ability

open class JsResult(
    private val receiver: ResultReceiver
) {
    var result = false
        private set

    fun confirm() {
        result = true
        wakeUp()
    }

    fun cancel() {
        result = false
        wakeUp()
    }

    private fun wakeUp() {
        receiver.onJsResultComplete(this)
    }

    interface ResultReceiver {
        fun onJsResultComplete(result: JsResult)
    }
}