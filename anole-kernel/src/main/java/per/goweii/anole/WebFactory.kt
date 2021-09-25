package per.goweii.anole

import android.content.Context
import per.goweii.anole.kernel.WebKernel

object WebFactory {
    private var instanceCreator: WebInstanceCreator? = null
    private var instanceInitializers: ArrayList<WebInstanceInitializer> = ArrayList()

    fun setInstanceBuilder(instanceCreator: WebInstanceCreator) {
        this.instanceCreator = instanceCreator
    }

    fun addInstanceInitializer(instanceInitializer: WebInstanceInitializer) {
        instanceInitializers.add(instanceInitializer)
    }

    fun removeInstanceInitializer(instanceInitializer: WebInstanceInitializer) {
        instanceInitializers.remove(instanceInitializer)
    }

    fun clearInstanceInitializer() {
        instanceInitializers.clear()
    }

    fun create(context: Context): WebKernel {
        if (instanceCreator == null) {
            throw NullPointerException("instanceBuilder == null")
        }
        val webKernel = instanceCreator!!.build(context)
        instanceInitializers.forEach { it.initialize(webKernel) }
        return webKernel
    }
}