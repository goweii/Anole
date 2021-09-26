package per.goweii.android.anole.utils

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import per.goweii.android.anole.R
import java.util.*

class DefSearch(private val application: Application) {
    private val sp by lazy { application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE) }

    private val customSearchList = arrayListOf(
        CustomSearch("https://www.baidu.com/s?wd=%s").apply { logoRes = R.drawable.ic_baidu },
        CustomSearch("https://www.google.com/search?q=%s").apply { logoRes = R.drawable.ic_google },
        CustomSearch("https://www.bing.com/search?q=%s").apply { logoRes = R.drawable.ic_bing },
    )

    fun getAllSearch(): List<CustomSearch> {
        val allSearch = arrayListOf<CustomSearch>()
        allSearch.addAll(customSearchList)
        return allSearch
    }

    fun getDefSearch(): CustomSearch {
        val url = sp.getString(KEY_DEF_SEARCH_URL, null)
        val logo = sp.getString(KEY_DEF_SEARCH_LOGO, null)
        return if (url.isNullOrBlank()) {
            customSearchList.first()
        } else {
            if (!logo.isNullOrBlank()) {
                CustomSearch(url).apply { logoUrl = logo }
            } else {
                customSearchList
                    .find { Url.parse(url).host == Url.parse(it.url).host }
                    ?: CustomSearch(url)
            }
        }
    }

    fun saveDefSearch(customSearch: CustomSearch) {
        sp.edit {
            putString(KEY_DEF_SEARCH_URL, customSearch.url)
            putString(KEY_DEF_SEARCH_LOGO, customSearch.logoUrl)
        }
    }

    companion object {
        private const val SP_NAME = "def_search"

        private const val KEY_DEF_SEARCH_LOGO = "def_search_logo"
        private const val KEY_DEF_SEARCH_URL = "def_search_url"

        private var sInstance: DefSearch? = null

        @Synchronized
        fun getInstance(context: Context): DefSearch {
            if (sInstance == null) {
                synchronized(DefSearch::class.java) {
                    if (sInstance == null) {
                        sInstance = DefSearch(context.applicationContext as Application)
                    }
                }
            }
            return sInstance!!
        }
    }

    data class CustomSearch(
        val url: String
    ) {
        var logoRes: Int? = null
        var logoUrl: String? = null

        fun getSearchUrl(keyword: String): String {
            return try {
                String.format(url, keyword)
            } catch (e: IllegalFormatException) {
                url
            }
        }
    }
}