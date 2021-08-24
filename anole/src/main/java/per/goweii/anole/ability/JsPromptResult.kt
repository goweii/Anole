package per.goweii.anole.ability

open class JsPromptResult(
    receiver: ResultReceiver
): JsResult(receiver) {
    var stringResult: String? = null
        private set

    fun confirm(stringResult: String?) {
        this.stringResult = stringResult
        confirm()
    }
}