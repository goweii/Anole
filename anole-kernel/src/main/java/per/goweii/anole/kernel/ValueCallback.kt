package per.goweii.anole.kernel

interface ValueCallback<T> {
    fun onReceiveValue(value: T)
}