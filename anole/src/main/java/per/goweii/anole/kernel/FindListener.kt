package per.goweii.anole.kernel

interface FindListener {
    fun onFindResultReceived(activeMatchOrdinal: Int, numberOfMatches: Int, isDoneCounting: Boolean)
}