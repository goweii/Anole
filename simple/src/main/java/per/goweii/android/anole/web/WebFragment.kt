package per.goweii.android.anole.web

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentWebBinding
import per.goweii.android.anole.home.Bookmark
import per.goweii.android.anole.home.BookmarkManager
import per.goweii.android.anole.main.MainViewModel
import per.goweii.android.anole.utils.*
import per.goweii.android.anole.window.WindowFragment
import per.goweii.android.anole.window.WindowFragmentDirections
import per.goweii.android.anole.window.WindowViewModel
import per.goweii.anole.ability.impl.BackForwardIconAbility
import per.goweii.anole.ability.impl.PageInfoAbility
import per.goweii.anole.ability.impl.ProgressAbility
import per.goweii.anole.kernel.WebKernel
import per.goweii.anole.kernel.WebSettings

class WebFragment : BaseFragment() {
    companion object {
        fun newInstance(webToken: WebToken): WebFragment {
            return WebFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(WebViewModel.ARG_WEB_TOKEN, webToken)
                }
            }
        }
    }

    private val mainViewModel by activityViewModels<MainViewModel>()
    private val windowViewModel by parentViewModels<WindowFragment, WindowViewModel>()
    private val allWebViewModel by parentViewModels<AllWebFragment, AllWebViewModel>()
    private val webViewModel by viewModels<WebViewModel>()

    private var _binding: FragmentWebBinding? = null
    private val binding get() = _binding!!

    private lateinit var backForwardIconAbility: BackForwardIconAbility
    private lateinit var pageInfoAbility: PageInfoAbility
    private lateinit var progressAbility: ProgressAbility

    private val webToken: WebToken get() = webViewModel.webToken
    private val webKernel: WebKernel get() = webViewModel.webKernel

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (webKernel.canGoBack) {
                webKernel.goBack()
            } else {
                windowViewModel.showHome()
            }
        }
    }

    val curUrl: String? get() = webKernel.url
    val curTitle: String? get() = webKernel.title
    val curFavicon: Bitmap? get() = webKernel.favicon

    fun loadUrl(url: String) {
        webKernel.loadUrl(url)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWebBinding.inflate(inflater, container, false)
        initSwipeDismiss()
        webKernel.settings.forceDark =
            when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
                Configuration.UI_MODE_NIGHT_YES -> WebSettings.ForceDark.FORCE_DARK_ON
                Configuration.UI_MODE_NIGHT_NO -> WebSettings.ForceDark.FORCE_DARK_OFF
                else -> WebSettings.ForceDark.FORCE_DARK_AUTO
            }
        webKernel.kernelView.removeFromParent()
        binding.webContainer.addView(
            webKernel.kernelView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        binding.webContainer.onTouch = { _ ->
            allWebViewModel.switchWeb(webToken)
        }
        binding.ivBack.setOnClickListener {
            if (webKernel.canGoBack) {
                webKernel.goBack()
            } else {
                if (webToken.subsidiary) {
                    allWebViewModel.removeWeb(webToken)
                }
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
            windowViewModel.newWindow(
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
                _binding ?: return@ProgressAbility
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
                _binding ?: return@PageInfoAbility
                binding.ivTopBookmark.isSelected = BookmarkManager.getInstance(requireContext())
                    .find(it ?: "") != null
            },
            onReceivedPageTitle = {
                _binding ?: return@PageInfoAbility
                binding.tvTitle.text = it ?: Url.parse(webKernel.url).host ?: ""
                binding.tvTopTitle.text = it ?: Url.parse(webKernel.url).host ?: ""
            },
            onReceivedPageIcon = {
                _binding ?: return@PageInfoAbility
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
                _binding ?: return@BackForwardIconAbility
                if (it) {
                    binding.ivBack.setImageResource(R.drawable.ic_back)
                    binding.ivBack.isEnabled = true
                    binding.ivBack.animate().alpha(1F).start()
                } else {
                    if (webToken.subsidiary) {
                        binding.ivBack.setImageResource(R.drawable.ic_close_window)
                        binding.ivBack.isEnabled = true
                        binding.ivBack.animate().alpha(1F).start()
                    } else {
                        binding.ivBack.setImageResource(R.drawable.ic_back)
                        binding.ivBack.isEnabled = false
                        binding.ivBack.animate().alpha(0.6F).start()
                    }
                }
            },
            canGoForward = {
                _binding ?: return@BackForwardIconAbility
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
        webKernel.settings.apply {
            javaScriptEnabled = false
        }
    }

    override fun onDestroyView() {
        binding.webContainer.removeView(webKernel.kernelView)
        _binding = null
        super.onDestroyView()
    }

    private fun initSwipeDismiss() {
        binding.swipeLayout.onDismiss = {
            allWebViewModel.removeWeb(webToken)
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

    var onGesture: ((e: MotionEvent) -> Unit)? = null
    var onGestureStart: (() -> Unit)? = null
    var onGestureScroll: ((dx: Float, dy: Float) -> Unit)? = null
    var onGestureEnd: ((vx: Float, vy: Float) -> Unit)? = null
}