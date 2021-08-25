package per.goweii.anole.utils

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.Process
import android.text.TextUtils

val Context.currentProcessName: String?
    get() = ProcessUtils.getCurrentProcessName(this)

object ProcessUtils {
    private var currentProcessName: String? = null

    fun getCurrentProcessName(context: Context): String? {
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName
        }
        currentProcessName = getCurrentProcessNameByApplication()
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName
        }
        currentProcessName = getCurrentProcessNameByActivityThread()
        if (!TextUtils.isEmpty(currentProcessName)) {
            return currentProcessName
        }
        currentProcessName = getCurrentProcessNameByActivityManager(context)
        return currentProcessName
    }

    private fun getCurrentProcessNameByApplication(): String? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            return Application.getProcessName()
        }
        return null
    }

    @SuppressLint("DiscouragedPrivateApi", "PrivateApi")
    private fun getCurrentProcessNameByActivityThread(): String? {
        var processName: String? = null
        try {
            val declaredMethod = Class.forName(
                "android.app.ActivityThread",
                false,
                Application::class.java.classLoader
            ).getDeclaredMethod("currentProcessName", *arrayOfNulls(0))
            declaredMethod.isAccessible = true
            val invoke = declaredMethod.invoke(null, *arrayOfNulls(0))
            if (invoke is String) {
                processName = invoke
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
        return processName
    }

    private fun getCurrentProcessNameByActivityManager(context: Context): String? {
        val pid = Process.myPid()
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningAppList = am.runningAppProcesses
        if (runningAppList != null) {
            for (processInfo in runningAppList) {
                if (processInfo.pid == pid) {
                    return processInfo.processName
                }
            }
        }
        return null
    }
}