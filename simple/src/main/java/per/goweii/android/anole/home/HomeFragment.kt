package per.goweii.android.anole.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentHomeBinding
import per.goweii.android.anole.utils.parentViewModelsByAndroid
import per.goweii.android.anole.utils.viewModelsByAndroid
import per.goweii.android.anole.window.WindowFragment
import per.goweii.android.anole.window.WindowFragmentDirections
import per.goweii.android.anole.window.WindowViewModel

class HomeFragment : BaseFragment() {
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
            bookmarkAdapter = BookmarkAdapter()
            binding.rvBookmark.layoutManager = GridLayoutManager(requireContext(), 4)
            binding.rvBookmark.adapter = bookmarkAdapter
            bookmarkAdapter?.onClickItem = {
                windowViewModel.loadUrlOnNewWindow(it.url)
            }
            bookmarkAdapter?.onLongClickItem = {
                windowViewModel.removeBookmark(it.url)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvSearch.setOnClickListener {
            binding.tvSearch.findNavController()
                .navigate(
                    WindowFragmentDirections.actionWindowFragmentToSearchFragment(),
                    FragmentNavigatorExtras(
                        binding.tvSearch to getString(R.string.transition_name_search)
                    )
                )
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.bookmarkLiveData.collect {
                bookmarkAdapter?.setData(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.addOrUpdateBookmarkSharedFlow.collect {
                addOrUpdateBookmark(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.removeBookmarkSharedFlow.collect {
                removeBookmark(it)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        this.windowViewModel.goBackEnableLiveData.postValue(false)
        this.windowViewModel.goForwardEnableLiveData.postValue(false)
    }

    private fun addOrUpdateBookmark(bookmark: Bookmark) {
        val oldBookmark = BookmarkManager.getInstance(requireContext()).add(bookmark)
        if (oldBookmark !== bookmark) {
            bookmarkAdapter?.updateData(bookmark)
        } else {
            bookmarkAdapter?.addData(bookmark)
        }
    }

    private fun removeBookmark(url: String?) {
        url ?: return
        val oldBookmark = BookmarkManager.getInstance(requireContext()).remove(url)
        if (oldBookmark != null) {
            bookmarkAdapter?.removeData(oldBookmark)
        }
    }
}