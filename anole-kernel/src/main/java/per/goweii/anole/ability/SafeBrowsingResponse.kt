package per.goweii.anole.ability

interface SafeBrowsingResponse {
    fun showInterstitial(allowReporting: Boolean)
    fun proceed(report: Boolean)
    fun backToSafety(report: Boolean)
}