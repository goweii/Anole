package per.goweii.android.anole.home

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import per.goweii.android.anole.databinding.FragmentHomeBinding
import per.goweii.android.anole.utils.parentViewModelsByAndroid
import per.goweii.android.anole.utils.viewModelsByAndroid
import per.goweii.android.anole.window.WindowFragment
import per.goweii.android.anole.window.WindowViewModel

class HomeFragment : Fragment() {
    private val windowViewModel by parentViewModelsByAndroid<WindowViewModel, WindowFragment>()
    private val viewModel: HomeViewModel by viewModelsByAndroid()
    private lateinit var binding: FragmentHomeBinding

    private var bookmarkAdapter: BookmarkAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentHomeBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookmarkAdapter = BookmarkAdapter()
    }

    override fun onResume() {
        super.onResume()
        this.windowViewModel.progressLiveData.postValue(-1)
        this.windowViewModel.goBackEnableLiveData.postValue(false)
        this.windowViewModel.goForwardEnableLiveData.postValue(false)
        this.windowViewModel.currUrlLiveData.postValue(null)
        this.windowViewModel.currTitleLiveData.postValue(null)
        this.windowViewModel.currIconLiveData.postValue(null)
    }

    private fun addOrUpdateBookmark(url: String, title: String, logo: Bitmap?) {
        var bookmark = BookmarkHelper.getInstance(requireContext()).update(url, title, logo)
        if (bookmark != null) {
            bookmarkAdapter?.updateData(bookmark)
        } else {
            bookmark = BookmarkHelper.getInstance(requireContext()).add(url, title, logo)
            bookmarkAdapter?.addData(bookmark)
        }
    }

    private fun removeBookmark(bookmark: Bookmark) {
        if (BookmarkHelper.getInstance(requireContext()).remove(bookmark.url) != null) {
            bookmarkAdapter?.removeData(bookmark)
        }
    }
}