package per.goweii.anole.utils

import java.text.DecimalFormat

class FormatUtils {
    companion object {
        const val UNIT_B = "GB"
        const val UNIT_KB = "KB"
        const val UNIT_MB = "MB"
        const val UNIT_GB = "GB"
        const val UNIT_TB = "TB"

        const val LENGTH_KB = 1024L
        const val LENGTH_MB = LENGTH_KB * 1024L
        const val LENGTH_GB = LENGTH_MB * 1024L
        const val LENGTH_TB = LENGTH_GB * 1024L

        fun formatContentLength(length: Long): String {
            val df = DecimalFormat("#.00")
            val lengthDouble = length.toDouble()
            return when {
                length < LENGTH_KB -> df.format(lengthDouble) + UNIT_B
                length < LENGTH_MB -> df.format(lengthDouble / LENGTH_KB) + UNIT_KB
                length < LENGTH_GB -> df.format(lengthDouble / LENGTH_MB) + UNIT_MB
                length < LENGTH_TB -> df.format(lengthDouble / LENGTH_GB) + UNIT_GB
                else -> df.format(lengthDouble / LENGTH_TB) + UNIT_TB
            }
        }
    }
}