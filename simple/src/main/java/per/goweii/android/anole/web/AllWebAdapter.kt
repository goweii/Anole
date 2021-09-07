package per.goweii.android.anole.web

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import per.goweii.android.anole.utils.WebInitConfig

class AllWebAdapter(fragment: AllWebFragment) :
    FragmentStateAdapter(fragment.childFragmentManager, fragment.lifecycle) {
    private val webInitConfigList = arrayListOf<WebInitConfig>()

    fun indexOf(webInitConfig: WebInitConfig): Int {
        return webInitConfigList.indexOf(webInitConfig)
    }

    fun indexOf(kernelId: Int): Int {
        return webInitConfigList.indexOfFirst { it.kernelId == kernelId }
    }

    fun addWeb(webInitConfig: WebInitConfig) {
        webInitConfigList.add(webInitConfig)
        notifyItemInserted(webInitConfigList.lastIndex)
    }

    fun removeWeb(webInitConfig: WebInitConfig) {
        val index = webInitConfigList.indexOf(webInitConfig)
        removeWebAt(index)
    }

    fun removeWebAt(index: Int) {
        if (index in webInitConfigList.indices) {
            webInitConfigList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun getItemCount(): Int {
        return webInitConfigList.size
    }

    override fun getItemId(position: Int): Long {
        return System.identityHashCode(webInitConfigList[position]).toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return webInitConfigList.find { System.identityHashCode(it).toLong() == itemId } != null
    }

    override fun createFragment(position: Int): Fragment {
        val initConfig = webInitConfigList[position]
        return WebFragment.newInstance(initConfig)
    }
}