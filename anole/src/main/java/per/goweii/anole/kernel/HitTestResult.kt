package per.goweii.anole.kernel

data class HitTestResult(
    val type: Int,
    val extra: String?
) {
    companion object {
        @Deprecated("")
        val ANCHOR_TYPE = 1
        const val EDIT_TEXT_TYPE = 9
        const val EMAIL_TYPE = 4
        const val GEO_TYPE = 3

        @Deprecated("")
        val IMAGE_ANCHOR_TYPE = 6
        const val IMAGE_TYPE = 5
        const val PHONE_TYPE = 2
        const val SRC_ANCHOR_TYPE = 7
        const val SRC_IMAGE_ANCHOR_TYPE = 8
        const val UNKNOWN_TYPE = 0
    }
}