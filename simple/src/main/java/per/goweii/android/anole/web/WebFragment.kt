package per.goweii.android.anole.web

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import per.goweii.android.anole.R
import per.goweii.android.anole.databinding.FragmentWebBinding
import per.goweii.android.anole.home.Bookmark
import per.goweii.android.anole.home.BookmarkManager
import per.goweii.android.anole.utils.parentViewModelsByAndroid
import per.goweii.android.anole.window.WindowFragment
import per.goweii.android.anole.window.WindowViewModel
import per.goweii.anole.WebFactory
import per.goweii.anole.ability.impl.BackForwardIconAbility
import per.goweii.anole.ability.impl.PageInfoAbility
import per.goweii.anole.ability.impl.ProgressAbility
import per.goweii.anole.view.KernelView

class WebFragment : Fragment() {
    companion object {
        private const val ARG_INITIAL_URL = "initial_url"

        fun newInstance(initialUrl: String?): WebFragment {
            return WebFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_INITIAL_URL, initialUrl)
                }
            }
        }
    }

    private val windowViewModel by parentViewModelsByAndroid<WindowViewModel, WindowFragment>()
    private val allWebViewModel by parentViewModelsByAndroid<AllWebViewModel, AllWebFragment>()

    private lateinit var binding: FragmentWebBinding
    private lateinit var kernelView: KernelView

    private lateinit var backForwardIconAbility: BackForwardIconAbility
    private lateinit var pageInfoAbility: PageInfoAbility
    private lateinit var progressAbility: ProgressAbility

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentWebBinding.inflate(inflater, container, false)
            initSwipeDismiss()
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.webContainer.onTouch = { _ ->
            allWebViewModel.onTouchedWebFragment(this@WebFragment)
        }
        kernelView =
            createWeb(arguments?.getString(ARG_INITIAL_URL) ?: getString(R.string.initial_url))
        backForwardIconAbility = BackForwardIconAbility(
            canGoBack = { windowViewModel.goBackEnableLiveData.postValue(it) },
            canGoForward = { windowViewModel.goForwardEnableLiveData.postValue(it) }
        )
        progressAbility = ProgressAbility(
            onProgress = { windowViewModel.progressLiveData.postValue(it) }
        )
        pageInfoAbility = PageInfoAbility(
            onReceivedPageUrl = { windowViewModel.currUrlLiveData.postValue(it) },
            onReceivedPageTitle = { windowViewModel.currTitleLiveData.postValue(it) },
            onReceivedPageIcon = { windowViewModel.currIconLiveData.postValue(it) }
        )
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.goBackOrForwardSharedFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
                .collect { kernelView.goBackOrForward(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.loadUrlSharedFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
                .collect { kernelView.loadUrl(it ?: getString(R.string.initial_url)) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.reloadSharedFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
                .collect { kernelView.reload() }
        }
    }

    override fun onResume() {
        super.onResume()
        kernelView.onResume()
        kernelView.settings.apply {
            javaScriptEnabled = true
        }
        if (!kernelView.webClient.containsAbility(backForwardIconAbility)) {
            kernelView.webClient.addAbility(backForwardIconAbility)
        }
        if (!kernelView.webClient.containsAbility(progressAbility)) {
            kernelView.webClient.addAbility(progressAbility)
        }
        if (!kernelView.webClient.containsAbility(pageInfoAbility)) {
            kernelView.webClient.addAbility(pageInfoAbility)
        }
    }

    override fun onPause() {
        super.onPause()
        kernelView.webClient.removeAbility(backForwardIconAbility)
        kernelView.webClient.removeAbility(progressAbility)
        kernelView.webClient.removeAbility(pageInfoAbility)
        kernelView.onPause()
        kernelView.settings.apply {
            javaScriptEnabled = false
        }
    }

    private fun createWeb(url: String): KernelView {
        val kernelView = WebFactory.with(requireContext())
            .applyDefaultConfig()
            .attachTo(binding.webContainer)
            .bindToLifecycle(viewLifecycleOwner)
            .get()
        kernelView.loadUrl(url)
        return kernelView
    }

    private fun initSwipeDismiss() {
        binding.swipeLayout.onDismiss = {
            allWebViewModel.onRemoveWebFragment(this@WebFragment)
        }
        binding.swipeLayout.onCollect = {
            if (!kernelView.url.isNullOrBlank()) {
                val url = kernelView.url!!
                if (BookmarkManager.getInstance(requireContext()).find(url) != null) {
                    windowViewModel.removeBookmark(url)
                } else {
                    windowViewModel.addOrUpdateBookmark(
                        Bookmark(
                            url,
                            kernelView.title,
                            kernelView.favicon
                        )
                    )
                }
            }
        }
    }
}