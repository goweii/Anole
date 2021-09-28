package per.goweii.android.anole.home

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentHomeBinding
import per.goweii.android.anole.utils.DefHome
import per.goweii.android.anole.utils.parentViewModels
import per.goweii.android.anole.window.WindowFragment
import per.goweii.android.anole.window.WindowFragmentDirections
import per.goweii.android.anole.window.WindowViewModel

class HomeFragment : BaseFragment() {
    private val windowViewModel by parentViewModels<WindowFragment, WindowViewModel>()
    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var bookmarkAdapter: BookmarkAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (bookmarkAdapter == null) {
            bookmarkAdapter = BookmarkAdapter()
        }
        binding.rvBookmark.layoutManager =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                GridLayoutManager(requireContext(), 6, GridLayoutManager.VERTICAL, false)
            } else {
                GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
            }
        binding.rvBookmark.adapter = bookmarkAdapter
        bookmarkAdapter?.onClickItem = {
            windowViewModel.newWindow(it.url)
        }
        bookmarkAdapter?.onLongClickItem = {
            windowViewModel.removeBookmark(it.url)
        }
        binding.tvSearch.setOnClickListener {
            binding.tvSearch.findNavController()
                .navigate(
                    WindowFragmentDirections.actionWindowFragmentToSearchFragment(null),
                    FragmentNavigatorExtras(
                        binding.tvSearch to getString(R.string.transition_name_search)
                    )
                )
        }
        binding.cvMenu.setOnClickListener {
            findNavController().navigate(
                WindowFragmentDirections.actionWindowFragmentToMenuDialogFragment()
            )
        }
        binding.cvWindows.setOnClickListener {
            val count = windowViewModel.windowCountLiveData.value?.coerceAtLeast(0) ?: 0
            if (count == 0) {
                windowViewModel.newWindow(
                    DefHome.getInstance(requireContext()).getDefHome()
                )
            } else {
                windowViewModel.showWeb()
            }
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
        windowViewModel.windowCountLiveData.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.tvWindowsCount.text = it.toString()
            } else {
                binding.tvWindowsCount.text = getString(R.string.add_window)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        _binding?.rvBookmark?.let {
            val lm = it.layoutManager
            if (lm is GridLayoutManager) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    lm.spanCount = 6
                } else {
                    lm.spanCount = 4
                }
                it.requestLayout()
            }
        }
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