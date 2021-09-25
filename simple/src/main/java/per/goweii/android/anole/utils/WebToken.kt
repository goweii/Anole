package per.goweii.android.anole.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

/**
 * Web令牌
 *
 * 每一个Web窗口创建前，需要这样一个配置，将配置传给Fragment，由Fragment通关kernelId来获取Web实例
 * @see WebInstance
 *
 * @param initialUrl 初始url
 * @param subsidiary 是否是附属窗口，即子窗口，表示由其他窗口通过js等打开的窗口。
 *                   该类型的窗口应该在回退栈为空时触发关闭。
 * @param kernelId 内核id，标识唯一性
 */
@Parcelize
data class WebToken(
    val initialUrl: String?,
    val subsidiary: Boolean = false,
    val kernelId: Int = UUID.randomUUID().hashCode()
) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as WebToken
        if (kernelId != other.kernelId) return false
        return true
    }

    override fun hashCode(): Int {
        return kernelId
    }
}
