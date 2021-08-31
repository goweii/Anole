package per.goweii.android.anole.web

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class AllWebAdapter(fragment: AllWebFragment) : FragmentStateAdapter(fragment) {
    private val webFragments = arrayListOf<WebFragment>()

    fun indexOf(webFragment: WebFragment): Int {
        return webFragments.indexOf(webFragment)
    }

    fun addWeb(webFragment: WebFragment) {
        webFragments.add(webFragment)
        notifyItemInserted(webFragments.lastIndex)
    }

    fun removeWeb(webFragment: WebFragment) {
        val index = webFragments.indexOf(webFragment)
        removeWebAt(index)
    }

    fun removeWebAt(index: Int) {
        if (index in webFragments.indices) {
            webFragments.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    override fun getItemCount(): Int {
        return webFragments.size
    }

    override fun getItemId(position: Int): Long {
        return System.identityHashCode(webFragments[position]).toLong()
    }

    override fun createFragment(position: Int): Fragment {
        return webFragments[position]
    }
}