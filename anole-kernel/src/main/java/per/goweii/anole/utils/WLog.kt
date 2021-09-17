package per.goweii.anole.utils

import android.text.TextUtils
import android.util.Log
import per.goweii.anole.BuildConfig

object WLog {
    const val NONE = 0

    const val VERBOSE = 6
    const val DEBUG = 5
    const val INFO = 4
    const val WARN = 3
    const val ERROR = 2
    const val ASSERT = 1

    var defTag: String = "anole"
    var filter: Int = NONE

    init {
        if (BuildConfig.DEBUG && TextUtils.equals(BuildConfig.BUILD_TYPE, "debug")) {
            filter = VERBOSE
        }
    }

    fun v(tag: String? = null, msg: CharSequence?) {
        println(VERBOSE, tag, msg)
    }

    fun d(tag: String? = null, msg: CharSequence?) {
        println(DEBUG, tag, msg)
    }

    fun i(tag: String? = null, msg: CharSequence?) {
        println(INFO, tag, msg)
    }

    fun w(tag: String? = null, msg: CharSequence?) {
        println(WARN, tag, msg)
    }

    fun e(tag: String? = null, msg: CharSequence?) {
        println(ERROR, tag, msg)
    }

    fun a(tag: String? = null, msg: CharSequence?) {
        println(ASSERT, tag, msg)
    }

    inline fun v(tag: String? = null, msg: () -> CharSequence?) {
        println(VERBOSE, tag, msg())
    }

    inline fun d(tag: String? = null, msg: () -> CharSequence?) {
        println(DEBUG, tag, msg())
    }

    inline fun i(tag: String? = null, msg: () -> CharSequence?) {
        println(INFO, tag, msg())
    }

    inline fun w(tag: String? = null, msg: () -> CharSequence?) {
        println(WARN, tag, msg())
    }

    inline fun e(tag: String? = null, msg: () -> CharSequence?) {
        println(ERROR, tag, msg())
    }

    inline fun a(tag: String? = null, msg: () -> CharSequence?) {
        println(ASSERT, tag, msg())
    }

    fun println(priority: Int, tag: String?, msg: CharSequence?) {
        if (priority < filter) return
        Log.println(
            priority,
            tag ?: defTag,
            msg?.toString() ?: ""
        )
    }
}