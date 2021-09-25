package per.goweii.anole

import per.goweii.anole.kernel.WebKernel

interface WebInstanceInitializer {
    fun initialize(webKernel: WebKernel)
}