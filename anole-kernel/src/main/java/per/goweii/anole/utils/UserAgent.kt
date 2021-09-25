package per.goweii.anole.utils

import android.content.Context
import android.os.Build

class UserAgent {
    companion object {
        fun from(userAgentString: String): UserAgent {
            return UserAgent().apply {
                parse(userAgentString)
            }
        }
    }

    private val uaList = arrayListOf<String>()

    /**
     * Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Mobile Safari/537.36
     */
    fun parse(userAgentString: String) = apply {
        uaList.clear()
        val builder = StringBuilder()
        var enterCount = -1
        val chars = userAgentString.toCharArray()
        for (char in chars) {
            if (char == '(') {
                if (enterCount == -1) {
                    enterCount = 0
                }
                enterCount++
            } else if (char == ')') {
                enterCount--
            } else if (char == ' ') {
                if (enterCount <= 0) {
                    if (builder.isNotEmpty()) {
                        val value = builder.toString()
                        if (enterCount == 0) {
                            if (uaList.isNotEmpty()) {
                                val lastValue = uaList.removeAt(uaList.size - 1)
                                uaList.add("$lastValue $value")
                            } else {
                                uaList.add(value)
                            }
                        } else {
                            uaList.add(value)
                        }
                    }
                    enterCount = -1
                    builder.clear()
                    continue
                }
            }
            builder.append(char)
        }
    }

    fun append(value: String) = apply {
        uaList.add(value.trim())
    }

    fun append(appName: String, appVersionName: String, vararg extInfo: String) = apply {
        val ua = StringBuilder()
        ua.append(appName).append("/").append(appVersionName)
        if (extInfo.isNotEmpty()) {
            ua.append(" (")
            if (extInfo.size == 1) {
                ua.append(extInfo[0])
            } else {
                extInfo.forEachIndexed { index, s ->
                    if (index > 0) ua.append("; ")
                    ua.append(s)
                }
            }
            ua.append(")")
        }
        append(ua.toString())
    }

    fun appendDef(context: Context) = apply {
        val pm = context.packageManager
        val appName = pm.getApplicationLabel(context.applicationInfo).toString()
        val appVersionName = pm.getPackageInfo(context.packageName, 0).versionName
        val android = "Android ${Build.VERSION.RELEASE}"
        val sdk = "SDK ${Build.VERSION.SDK_INT}"
        val model = "MODEL ${Build.MODEL}"
        append(appName, appVersionName, android, sdk, model)
    }

    override fun toString(): String {
        val builder = StringBuilder()
        uaList.forEachIndexed { index, s ->
            if (index > 0) {
                builder.append(" ")
            }
            builder.append(s)
        }
        return builder.toString()
    }
}