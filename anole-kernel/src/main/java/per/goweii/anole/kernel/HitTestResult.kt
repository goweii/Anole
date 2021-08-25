package per.goweii.anole.kernel

data class HitTestResult(
    val type: Int,
    val extra: String?
) {
    enum class Type {
        UNKNOWN_TYPE,
        ANCHOR_TYPE,
        PHONE_TYPE,
        GEO_TYPE,
        EMAIL_TYPE,
        IMAGE_TYPE,
        IMAGE_ANCHOR_TYPE,
        SRC_ANCHOR_TYPE,
        SRC_IMAGE_ANCHOR_TYPE,
        EDIT_TEXT_TYPE
    }

    companion object {
        const val UNKNOWN_TYPE = 0
        const val ANCHOR_TYPE = 1
        const val PHONE_TYPE = 2
        const val GEO_TYPE = 3
        const val EMAIL_TYPE = 4
        const val IMAGE_TYPE = 5
        const val IMAGE_ANCHOR_TYPE = 6
        const val SRC_ANCHOR_TYPE = 7
        const val SRC_IMAGE_ANCHOR_TYPE = 8
        const val EDIT_TEXT_TYPE = 9
    }
}