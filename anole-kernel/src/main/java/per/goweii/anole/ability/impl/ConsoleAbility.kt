package per.goweii.anole.ability.impl

import android.util.Log
import per.goweii.anole.BuildConfig
import per.goweii.anole.R
import per.goweii.anole.ability.ConsoleMessage
import per.goweii.anole.ability.WebAbility
import per.goweii.anole.kernel.WebKernel

class ConsoleAbility : WebAbility() {
    private var tag = WebKernel::class.java.name

    override fun onAttachToKernel(kernel: WebKernel) {
        super.onAttachToKernel(kernel)
        tag = kernel.kernelView.context.getString(R.string.anole_console_tag)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        if (!BuildConfig.DEBUG) return false
        consoleMessage ?: return false
        val level = consoleMessage.level
        val msg = consoleMessage.message
        val sourceId = consoleMessage.sourceId
        val lineNumber = consoleMessage.lineNumber
        val priority = when (level) {
            ConsoleMessage.MessageLevel.VERBOSE -> Log.VERBOSE
            ConsoleMessage.MessageLevel.INFO -> Log.INFO
            ConsoleMessage.MessageLevel.WARNING -> Log.WARN
            ConsoleMessage.MessageLevel.ERROR -> Log.ERROR
            ConsoleMessage.MessageLevel.DEBUG -> Log.DEBUG
        }
        Log.println(priority, tag, "[$sourceId($lineNumber)]:$msg")
        return true
    }
}