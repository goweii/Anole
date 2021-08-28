package per.goweii.android.anole.main

data class MenuAction(
    val name: String,
    val icon: Int,
    val action: () -> Unit
)