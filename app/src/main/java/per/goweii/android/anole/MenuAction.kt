package per.goweii.android.anole

data class MenuAction(
    val name: String,
    val icon: Int,
    val action: () -> Unit
)