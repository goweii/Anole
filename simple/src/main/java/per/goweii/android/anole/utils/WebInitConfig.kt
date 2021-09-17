package per.goweii.android.anole.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Web创建初始配置
 *
 * 每一个Web窗口创建前，需要这样一个配置，将配置传给Fragment，由Fragment来创建或用key查找Web实例
 * @see WebInstance
 */
@Parcelize
data class WebInitConfig(
    val initialUrl: String?,
    val kernelId: Int = UUID.randomUUID().hashCode()
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as WebInitConfig
        if (kernelId != other.kernelId) return false
        return true
    }

    override fun hashCode(): Int {
        return kernelId
    }
}
