package per.goweii.anole.ability.impl

import android.util.Log
import android.webkit.ConsoleMessage
import per.goweii.anole.BuildConfig
import per.goweii.anole.R
import per.goweii.anole.ability.AnoleAbility
import per.goweii.anole.view.AnoleView

class ConsoleAbility : AnoleAbility() {
    private var tag = "chrome"

    override fun onAttachToWebView(webView: AnoleView) {
        super.onAttachToWebView(webView)
        tag = webView.context.getString(R.string.anole_console_tag)
    }

    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
        if (!BuildConfig.DEBUG) return false
        consoleMessage ?: return false
        val messageLevel = consoleMessage.messageLevel()
        val msg = consoleMessage.message()
        val sourceId = consoleMessage.sourceId()
        val lineNumber = consoleMessage.lineNumber()
        val priority = when (messageLevel) {
            ConsoleMessage.MessageLevel.TIP -> Log.VERBOSE
            ConsoleMessage.MessageLevel.LOG -> Log.INFO
            ConsoleMessage.MessageLevel.WARNING -> Log.WARN
            ConsoleMessage.MessageLevel.ERROR -> Log.ERROR
            ConsoleMessage.MessageLevel.DEBUG -> Log.DEBUG
            else -> Log.VERBOSE
        }
        Log.println(priority, tag, "[$sourceId($lineNumber)]:$msg")
        return true
    }
}