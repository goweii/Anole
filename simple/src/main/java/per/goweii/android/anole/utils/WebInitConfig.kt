package per.goweii.android.anole.utils

import java.util.*

/**
 * Web创建初始配置
 *
 * 每一个Web窗口创建前，需要这样一个配置，将配置传给Fragment，由Fragment来创建或用key查找Web实例
 * @see WebInstance
 */
data class WebInitConfig(
    val initialUrl: String?,
    val kernelId: Int = UUID.randomUUID().hashCode()
)
