package per.goweii.android.anole.web

import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter
import per.goweii.android.anole.utils.WebToken
import per.goweii.android.anole.utils.WebTokenDiffCallback

class AllWebAdapter(fragment: AllWebFragment) :
    FragmentStateAdapter(fragment.childFragmentManager, fragment.lifecycle) {
    private val webTokenList = arrayListOf<WebToken>()

    fun submitWebTokens(list: List<WebToken>) {
        val diffCallback = WebTokenDiffCallback(webTokenList, list)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        webTokenList.clear()
        webTokenList.addAll(list)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun getItemCount(): Int {
        return webTokenList.size
    }

    override fun getItemId(position: Int): Long {
        return webTokenList[position].hashCode().toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return webTokenList.find { it.hashCode().toLong() == itemId } != null
    }

    override fun createFragment(position: Int): Fragment {
        val initConfig = webTokenList[position]
        return WebFragment.newInstance(initConfig)
    }
}