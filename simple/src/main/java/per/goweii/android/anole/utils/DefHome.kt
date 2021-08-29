package per.goweii.android.anole.utils

import android.app.Application
import android.content.Context
import androidx.core.content.edit

class DefHome(private val application: Application) {
    private val sp by lazy { application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE) }

    fun getDefHome(): String? {
        return sp.getString(KEY_DEF_HOME_URL, null)
    }

    fun saveDefHome(url: String?) {
        sp.edit { putString(KEY_DEF_HOME_URL, url) }
    }

    companion object {
        private const val SP_NAME = "def_home"

        private const val KEY_DEF_HOME_URL = "def_home_url"

        private var sInstance: DefHome? = null

        @Synchronized
        fun getInstance(context: Context): DefHome {
            if (sInstance == null) {
                synchronized(DefHome::class.java) {
                    if (sInstance == null) {
                        sInstance = DefHome(context.applicationContext as Application)
                    }
                }
            }
            return sInstance!!
        }
    }
}