package per.goweii.android.anole.utils

import android.content.Context

object ThemeUtils {
    private val tempArray = IntArray(1)

    fun getThemeAttrColor(context: Context, attr: Int): Int {
        tempArray[0] = attr
        val a = context.obtainStyledAttributes(null, tempArray)
        return try {
            a.getColor(0, 0)
        } finally {
            a.recycle()
        }
    }
}