package per.goweii.anole.ability

data class ConsoleMessage(
    val message: String?,
    val sourceId: String? = null,
    val lineNumber: Int = 0,
    val level: MessageLevel = MessageLevel.VERBOSE
) {
    enum class MessageLevel {
        VERBOSE,
        DEBUG,
        INFO,
        WARNING,
        ERROR,
        ;
    }
}