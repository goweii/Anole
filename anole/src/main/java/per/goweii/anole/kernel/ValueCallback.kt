package per.goweii.anole.kernel

interface ValueCallback<T : Any?> {
    fun onReceiveValue(value: T)
}