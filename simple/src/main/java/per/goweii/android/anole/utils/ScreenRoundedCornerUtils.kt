package per.goweii.android.anole.utils

import android.os.Build
import android.view.RoundedCorner
import android.view.View

object ScreenRoundedCornerUtils {
    fun getScreenRoundedCornerRadius(view: View): Float {
        val brand = Build.BRAND ?: ""
        val getter: ScreenRoundedCornerRadiusGetter =
            if (brand.equals("Xiaomi", true)) {
                MIUIScreenRoundedCornerRadiusGetter()
            } else {
                AndroidSScreenRoundedCornerRadiusGetter()
            }
        return getter.getRoundedCornerRadius(view)
    }

    private interface ScreenRoundedCornerRadiusGetter {
        fun getRoundedCornerRadius(view: View): Float
    }

    private class MIUIScreenRoundedCornerRadiusGetter : ScreenRoundedCornerRadiusGetter {
        override fun getRoundedCornerRadius(view: View): Float {
            val resources = view.context.resources
            val radiusTop = resources.getIdentifier("rounded_corner_radius_top", "dimen", "android")
                .takeIf { it > 0 }
                ?.let { resources.getDimensionPixelSize(it) } ?: 0
            val radiusBottom = resources.getIdentifier("rounded_corner_radius_bottom", "dimen", "android")
                .takeIf { it > 0 }
                ?.let { resources.getDimensionPixelSize(it) } ?: 0
            return (radiusTop + radiusBottom) / 2F
        }
    }

    private class AndroidSScreenRoundedCornerRadiusGetter : ScreenRoundedCornerRadiusGetter {
        override fun getRoundedCornerRadius(view: View): Float {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val topLeft = view.rootWindowInsets.getRoundedCorner(RoundedCorner.POSITION_TOP_LEFT)?.radius ?: 0
                val topRight = view.rootWindowInsets.getRoundedCorner(RoundedCorner.POSITION_TOP_RIGHT)?.radius ?: 0
                val bottomLeft = view.rootWindowInsets.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_LEFT)?.radius ?: 0
                val bottomRight = view.rootWindowInsets.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_RIGHT)?.radius ?: 0
                (topLeft + topRight + bottomLeft + bottomRight) / 4F
            } else 0F
        }
    }
}