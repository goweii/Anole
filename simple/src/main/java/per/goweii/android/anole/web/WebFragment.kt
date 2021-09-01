package per.goweii.android.anole.web

import android.animation.Animator
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentWebBinding
import per.goweii.android.anole.home.Bookmark
import per.goweii.android.anole.home.BookmarkManager
import per.goweii.android.anole.main.ChoiceDefSearchAdapter
import per.goweii.android.anole.utils.DefSearch
import per.goweii.android.anole.utils.parentViewModelsByAndroid
import per.goweii.android.anole.window.WindowFragment
import per.goweii.android.anole.window.WindowViewModel
import per.goweii.anole.WebFactory
import per.goweii.anole.ability.impl.BackForwardIconAbility
import per.goweii.anole.ability.impl.PageInfoAbility
import per.goweii.anole.ability.impl.ProgressAbility
import per.goweii.anole.view.KernelView
import per.goweii.layer.core.Layer
import per.goweii.layer.core.anim.CommonAnimatorCreator
import per.goweii.layer.popup.PopupLayer
import per.goweii.layer.visualeffectview.PopupShadowLayout

class WebFragment : BaseFragment() {
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
        kernelView = createWeb(
            arguments?.getString(ARG_INITIAL_URL)
                ?: getString(R.string.initial_url)
        )
        binding.touchableLayout.onTouch = { _ ->
            allWebViewModel.onTouchedWebFragment(this@WebFragment)
        }
        binding.urlInputView.apply {
            onDefSearch = { showChoiceDefSearchPopup(it) }
            onCollect = { windowViewModel.addOrUpdateBookmark(it) }
            onUnCollect = { windowViewModel.removeBookmark(it) }
            onEnter = { windowViewModel.loadUrl(it) }
            onSearch = {
                windowViewModel.loadUrl(
                    DefSearch.getInstance(requireContext()).getDefSearch().getSearchUrl(it)
                )
            }
        }
        progressAbility = ProgressAbility(
            onProgress = {
                if (binding.pb.max != 100) {
                    binding.pb.max = 100
                }
                if (it in 0..100) {
                    ObjectAnimator.ofInt(binding.pb, "progress", binding.pb.progress, it).start()
                    binding.pb.animate().alpha(1F).start()
                } else {
                    binding.pb.progress = 0
                    binding.pb.animate().alpha(0F).start()
                }
            }
        )
        pageInfoAbility = PageInfoAbility(
            onReceivedPageUrl = {
                binding.urlInputView.setUrl(it)
                if (it == null) {
                    binding.urlInputView.setCollected(false)
                } else {
                    binding.urlInputView.setCollected(
                        BookmarkManager.getInstance(requireContext()).find(it) != null
                    )
                }
            },
            onReceivedPageTitle = {
                binding.urlInputView.setTitle(it)
            },
            onReceivedPageIcon = {
                binding.urlInputView.setIcon(it)
            }
        )
        backForwardIconAbility = BackForwardIconAbility(
            canGoBack = { windowViewModel.goBackEnableLiveData.postValue(it) },
            canGoForward = { windowViewModel.goForwardEnableLiveData.postValue(it) }
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
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.addOrUpdateBookmarkSharedFlow.collect { bookmark ->
                binding.urlInputView.url?.let { url ->
                    if (bookmark.url == url) {
                        binding.urlInputView.setCollected(true)
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.removeBookmarkSharedFlow.collect {
                if (binding.urlInputView.url == it) {
                    binding.urlInputView.setCollected(false)
                }
            }
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

    private fun showChoiceDefSearchPopup(ivIcon: ImageView) {
        PopupLayer(ivIcon)
            .setContentView(R.layout.popup_choice_def_search)
            .setContentAnimator(
                object : Layer.AnimatorCreator {
                    override fun createInAnimator(view: View): Animator {
                        view as PopupShadowLayout
                        return CommonAnimatorCreator()
                            .addAttr(
                                CommonAnimatorCreator.ScaleAttr()
                                    .setPivot(view.realArrowOffset, 0F)
                                    .setFrom(0.2F, 0.2F)
                                    .setTo(1F, 1F)
                            )
                            .addAttr(
                                CommonAnimatorCreator.AlphaAttr()
                                    .from(0F)
                                    .to(1F)
                            )
                            .createInAnimator(view)
                    }

                    override fun createOutAnimator(view: View): Animator {
                        view as PopupShadowLayout
                        return CommonAnimatorCreator()
                            .addAttr(
                                CommonAnimatorCreator.ScaleAttr()
                                    .setPivot(view.realArrowOffset, 0F)
                                    .setFrom(0.2F, 0.2F)
                                    .setTo(1F, 1F)
                            )
                            .addAttr(
                                CommonAnimatorCreator.AlphaAttr()
                                    .from(0F)
                                    .to(1F)
                            )
                            .createOutAnimator(view)
                    }
                }
            )
            .addOnBindDataListener { layer ->
                val rv = layer.requireView<RecyclerView>(R.id.popup_choice_def_search_rv)
                rv.layoutManager = LinearLayoutManager(
                    rv.context, LinearLayoutManager.HORIZONTAL, false
                )
                rv.adapter = ChoiceDefSearchAdapter(
                    DefSearch.getInstance(requireContext()).getAllSearch()
                ).apply {
                    onChoice = {
                        DefSearch.getInstance(requireContext()).saveDefSearch(it)
                        layer.dismiss()
                    }
                }
            }
            .show()
    }
}