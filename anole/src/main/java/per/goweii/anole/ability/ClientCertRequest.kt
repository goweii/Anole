package per.goweii.anole.ability

import java.security.Principal
import java.security.PrivateKey
import java.security.cert.X509Certificate

abstract class ClientCertRequest(
    val keyTypes: Array<String?>?,
    val principals: Array<Principal?>?,
    val host: String?,
    val port: Int
) {
    abstract fun proceed(privateKey: PrivateKey?, chain: Array<X509Certificate?>?)
    abstract fun ignore()
    abstract fun cancel()
}