package per.goweii.anole.ability

import per.goweii.anole.kernel.ValueCallback

class GeolocationPermissions {
    interface Callback {
        fun invoke(origin: String?, allow: Boolean, retain: Boolean)
    }

    fun getOrigins(callback: ValueCallback<Set<String?>?>?) {}
    fun getAllowed(origin: String?, callback: ValueCallback<Boolean?>?) {}
    fun clear(origin: String?) {}
    fun allow(origin: String?) {}
    fun clearAll() {}
}