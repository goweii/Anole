package per.goweii.android.anole.utils

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UrlLoadEntry(
    val url: String,
    val newWindow: Boolean
) : Parcelable
