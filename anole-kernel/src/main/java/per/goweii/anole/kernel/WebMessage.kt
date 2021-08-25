package per.goweii.anole.kernel

class WebMessage @JvmOverloads constructor(
    val `data`: String,
    val ports: Array<WebMessagePort>? = null
)