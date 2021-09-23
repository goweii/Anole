package per.goweii.android.anole.web

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
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
import per.goweii.android.anole.main.MainViewModel
import per.goweii.android.anole.utils.*
import per.goweii.android.anole.window.WindowFragment
import per.goweii.android.anole.window.WindowFragmentDirections
import per.goweii.android.anole.window.WindowViewModel
import per.goweii.anole.ability.impl.BackForwardIconAbility
import per.goweii.anole.ability.impl.PageInfoAbility
import per.goweii.anole.ability.impl.ProgressAbility
import per.goweii.anole.kernel.WebKernel
import per.goweii.layer.core.Layer
import per.goweii.layer.core.anim.CommonAnimatorCreator
import per.goweii.layer.popup.PopupLayer
import per.goweii.layer.visualeffectview.PopupShadowLayout

class WebFragment : BaseFragment() {
    companion object {
        private const val ARG_INIT_CONFIG = "init_config"

        fun newInstance(initConfig: WebInitConfig): WebFragment {
            return WebFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_INIT_CONFIG, initConfig)
                }
            }
        }
    }

    private val mainViewModel by activityViewModelsByAndroid<MainViewModel>()
    private val windowViewModel by parentViewModelsByAndroid<WindowViewModel, WindowFragment>()
    private val allWebViewModel by parentViewModelsByAndroid<AllWebViewModel, AllWebFragment>()

    private lateinit var initConfig: WebInitConfig
    private lateinit var webKernel: WebKernel

    private var _binding: FragmentWebBinding? = null
    private val binding get() = _binding!!

    private lateinit var backForwardIconAbility: BackForwardIconAbility
    private lateinit var pageInfoAbility: PageInfoAbility
    private lateinit var progressAbility: ProgressAbility

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (webKernel.canGoBack) {
                webKernel.goBack()
            } else {
                windowViewModel.showHome()
            }
        }
    }

    val curUrl: String? get() = if (::webKernel.isInitialized) webKernel.url else null
    val curTitle: String? get() = if (::webKernel.isInitialized) webKernel.title else null
    val curFavicon: Bitmap? get() = if (::webKernel.isInitialized) webKernel.favicon else null

    fun loadUrl(url: String) {
        if (::webKernel.isInitialized) {
            webKernel.loadUrl(url)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initConfig = requireArguments().getParcelable(ARG_INIT_CONFIG)!!
        webKernel = WebInstance.getInstance(requireContext()).obtain(initConfig.kernelId)
        if (initConfig.kernelId != webKernel.hashCode()) {
            webKernel.loadUrl(initConfig.initialUrl ?: getString(R.string.initial_url))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebBinding.inflate(inflater, container, false)
        initSwipeDismiss()
        binding.webContainer.addView(
            webKernel.kernelView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        binding.webContainer.onTouch = { _ ->
            allWebViewModel.onTouchedWebFragment(initConfig)
        }
        binding.ivBack.setOnClickListener {
            if (webKernel.canGoBack) {
                webKernel.goBack()
            }
        }
        binding.ivForward.setOnClickListener {
            if (webKernel.canGoForward) {
                webKernel.goForward()
            }
        }
        binding.ivMenu.setOnClickListener {
            findNavController().navigate(
                WindowFragmentDirections.actionWindowFragmentToMenuDialogFragment()
            )
        }
        binding.ivHome.setOnClickListener {
            windowViewModel.showHome()
        }
        binding.cvCount.setOnClickListener {
            windowViewModel.switchChoiceMode(null)
        }
        binding.cvCount.setOnLongClickListener {
            windowViewModel.loadUrlOnNewWindow(
                DefHome.getInstance(requireContext()).getDefHome()
            )
            return@setOnLongClickListener true
        }
        MultiWindowGesture(binding.tvTitle) {
            onTouch = { e -> onGesture?.invoke(e) }
            onDragStart = { onGestureStart?.invoke() }
            onDragging = { dx, dy -> onGestureScroll?.invoke(dx, dy) }
            onDragEnd = { vx, vy -> onGestureEnd?.invoke(vx, vy) }
            onSingleTap = {
                findNavController().navigate(
                    WindowFragmentDirections.actionWindowFragmentToSearchFragment(curUrl, true),
                    FragmentNavigatorExtras(
                        binding.tvTitle to getString(R.string.transition_name_search)
                    )
                )
            }
        }
        progressAbility = ProgressAbility(
            onProgress = {
                if (binding.pb.max != 100) {
                    binding.pb.max = 100
                }
                if (it in 0..100) {
                    ObjectAnimator.ofInt(binding.pb, "progress", binding.pb.progress, it)
                        .start()
                    binding.pb.animate().alpha(1F).start()
                } else {
                    binding.pb.progress = 0
                    binding.pb.animate().alpha(0F).start()
                }
            }
        )
        pageInfoAbility = PageInfoAbility(
            onReceivedPageUrl = {
                binding.ivTopBookmark.isSelected = BookmarkManager.getInstance(requireContext())
                    .find(it ?: "") != null
            },
            onReceivedPageTitle = {
                binding.tvTitle.text = it ?: Url.parse(webKernel.url).host ?: ""
                binding.tvTopTitle.text = it ?: Url.parse(webKernel.url).host ?: ""
            },
            onReceivedPageIcon = {
                if (it != null) {
                    binding.ivLogo.setImageBitmap(it)
                    binding.ivTopLogo.setImageBitmap(it)
                } else {
                    binding.ivLogo.setImageResource(R.drawable.ic_browser)
                    binding.ivTopLogo.setImageResource(R.drawable.ic_browser)
                }
            }
        )
        backForwardIconAbility = BackForwardIconAbility(
            canGoBack = {
                binding.ivBack.isEnabled = it
                binding.ivBack.animate().alpha(if (it) 1F else 0.6F).start()
            },
            canGoForward = {
                binding.ivForward.isEnabled = it
                binding.ivForward.animate().alpha(if (it) 1F else 0.6F).start()
            }
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.doOnAttach {
            val topInset = ViewCompat.getRootWindowInsets(view)
                ?.getInsets(WindowInsetsCompat.Type.systemBars())?.top ?: 0
            binding.vStatusBar.layoutParams.height = topInset
            binding.llTop.layoutParams?.let { lp ->
                lp as ViewGroup.MarginLayoutParams
                lp.topMargin = topInset - resources.getDimensionPixelSize(
                    R.dimen.dimenIconButtonSizeDefault
                ) - resources.getDimensionPixelSize(
                    R.dimen.dimenMarginHalf
                )
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.goBackOrForwardSharedFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
                .collect { webKernel.goBackOrForward(it) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.loadUrlSharedFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
                .collect { webKernel.loadUrl(it ?: getString(R.string.initial_url)) }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            mainViewModel.reloadFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
                .collect { webKernel.reload() }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.addOrUpdateBookmarkSharedFlow.collect {
                binding.ivTopBookmark.isSelected = true
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.removeBookmarkSharedFlow.collect {
                binding.ivTopBookmark.isSelected = false
            }
        }
        windowViewModel.windowCountLiveData.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.tvCount.text = it.toString()
            } else {
                binding.tvCount.text = getString(R.string.add_window)
                windowViewModel.showHome()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        onBackPressedCallback.isEnabled = true
        webKernel.resumeTimers()
        webKernel.onResume()
        webKernel.settings.apply {
            javaScriptEnabled = true
        }
        if (!webKernel.webClient.containsAbility(backForwardIconAbility)) {
            webKernel.webClient.addAbility(backForwardIconAbility)
        }
        if (!webKernel.webClient.containsAbility(progressAbility)) {
            webKernel.webClient.addAbility(progressAbility)
        }
        if (!webKernel.webClient.containsAbility(pageInfoAbility)) {
            webKernel.webClient.addAbility(pageInfoAbility)
        }
    }

    override fun onPause() {
        super.onPause()
        onBackPressedCallback.isEnabled = false
        webKernel.webClient.removeAbility(backForwardIconAbility)
        webKernel.webClient.removeAbility(progressAbility)
        webKernel.webClient.removeAbility(pageInfoAbility)
        webKernel.onPause()
        webKernel.pauseTimers()
        webKernel.settings.apply {
            javaScriptEnabled = false
        }
    }

    override fun onDestroyView() {
        binding.webContainer.removeView(webKernel.kernelView)
        _binding = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        WebInstance.getInstance(requireContext()).release(initConfig.kernelId)
        webKernel.destroy()
    }

    private fun initSwipeDismiss() {
        binding.swipeLayout.onDismiss = {
            allWebViewModel.onRemoveWebFragment(initConfig)
        }
        binding.swipeLayout.onCollect = {
            if (!webKernel.url.isNullOrBlank()) {
                val url = webKernel.url!!
                if (BookmarkManager.getInstance(requireContext()).find(url) != null) {
                    windowViewModel.removeBookmark(url)
                } else {
                    windowViewModel.addOrUpdateBookmark(
                        Bookmark(
                            url,
                            webKernel.title,
                            webKernel.favicon
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

    var onGesture: ((e: MotionEvent) -> Unit)? = null
    var onGestureStart: (() -> Unit)? = null
    var onGestureScroll: ((dx: Float, dy: Float) -> Unit)? = null
    var onGestureEnd: ((vx: Float, vy: Float) -> Unit)? = null
}