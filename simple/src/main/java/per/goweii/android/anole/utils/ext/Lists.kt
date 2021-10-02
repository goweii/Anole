package per.goweii.android.anole.utils.ext

import java.util.*

fun <T> List<T>.move(fromPosition: Int, toPosition: Int): Boolean {
    if (this.isEmpty()) return false
    if (fromPosition < 0 || fromPosition > this.lastIndex) return false
    if (toPosition < 0 || toPosition > this.lastIndex) return false
    if (fromPosition == toPosition) return true
    if (fromPosition < toPosition) {
        for (i in fromPosition until toPosition) {
            Collections.swap(this, i, i + 1)
        }
    } else {
        for (i in fromPosition downTo toPosition + 1) {
            Collections.swap(this, i, i - 1)
        }
    }
    return true
}

fun <T> List<T>.move(fromItem: T, toItem: T): Boolean {
    val fromPosition = indexOf(fromItem)
    val toPosition = indexOf(toItem)
    return this.move(fromPosition, toPosition)
}