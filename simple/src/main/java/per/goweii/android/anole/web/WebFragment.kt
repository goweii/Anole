package per.goweii.android.anole.web

import android.animation.Animator
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.*
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
import per.goweii.anole.view.KernelView
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
    private lateinit var kernelView: KernelView

    private lateinit var binding: FragmentWebBinding

    private lateinit var backForwardIconAbility: BackForwardIconAbility
    private lateinit var pageInfoAbility: PageInfoAbility
    private lateinit var progressAbility: ProgressAbility

    private val onBackPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (kernelView.canGoBack) {
                kernelView.goBack()
            } else {
                // 不要删除这个窗口
                // allWebViewModel.onRemoveWebFragment(this@WebFragment)
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initConfig = requireArguments().getParcelable(ARG_INIT_CONFIG)!!
        kernelView = WebInstance.getInstance(requireContext()).get(initConfig.kernelId)
        kernelView.loadUrl(initConfig.initialUrl ?: getString(R.string.initial_url))
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWebBinding.inflate(inflater, container, false)
        initSwipeDismiss()
        binding.webContainer.addView(
            kernelView, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        binding.webContainer.onTouch = { _ ->
            allWebViewModel.onTouchedWebFragment(initConfig)
        }
        binding.ivBack.setOnClickListener {
            if (kernelView.canGoBack) {
                kernelView.goBack()
            }
        }
        binding.ivForward.setOnClickListener {
            if (kernelView.canGoForward) {
                kernelView.goForward()
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
        val gestureListener = GestureListenerImpl()
        val gestureDetector = GestureDetector(requireContext(), gestureListener)
        binding.tvTitle.setOnTouchListener { _, e ->
            gestureDetector.onTouchEvent(e)
            when (e.action) {
                MotionEvent.ACTION_UP -> {
                    gestureListener.onUp(e)
                }
                MotionEvent.ACTION_CANCEL -> {
                    gestureListener.onCancel(e)
                }
            }
            true
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
            },
            onReceivedPageTitle = {
                binding.tvTitle.text = it ?: Url.parse(kernelView.url).host ?: ""
            },
            onReceivedPageIcon = {
                if (it != null) {
                    binding.ivLogo.setImageBitmap(it)
                } else {
                    binding.ivLogo.setImageResource(R.drawable.ic_browser)
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
            binding.vStatusBar.layoutParams.height = ViewCompat.getRootWindowInsets(view)
                ?.getInsets(WindowInsetsCompat.Type.systemBars())?.top ?: 0
        }
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
            mainViewModel.reloadFlow
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.RESUMED)
                .collect { kernelView.reload() }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.addOrUpdateBookmarkSharedFlow.collect { bookmark ->
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            windowViewModel.removeBookmarkSharedFlow.collect {
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
        kernelView.resumeTimers()
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
        onBackPressedCallback.isEnabled = false
        kernelView.webClient.removeAbility(backForwardIconAbility)
        kernelView.webClient.removeAbility(progressAbility)
        kernelView.webClient.removeAbility(pageInfoAbility)
        kernelView.onPause()
        kernelView.pauseTimers()
        kernelView.settings.apply {
            javaScriptEnabled = false
        }
    }

    override fun onDestroyView() {
        binding.webContainer.removeView(kernelView)
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
        WebInstance.getInstance(requireContext()).remove(initConfig.kernelId)
        kernelView.destroy()
    }

    private fun initSwipeDismiss() {
        binding.swipeLayout.onDismiss = {
            allWebViewModel.onRemoveWebFragment(initConfig)
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

    private inner class GestureListenerImpl : GestureDetector.SimpleOnGestureListener() {
        private var startEvent: MotionEvent? = null
        private val scrolling get() = startEvent != null

        override fun onDown(e: MotionEvent): Boolean {
            if (scrolling) {
                startEvent = null
            }
            return true
        }

        fun onUp(e: MotionEvent) {
            if (scrolling) {
                startEvent = null
                onGestureEnd?.invoke()
            }
        }

        fun onCancel(e: MotionEvent) {
            if (scrolling) {
                startEvent = null
                onGestureEnd?.invoke()
            }
        }

        override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
            findNavController().navigate(
                WindowFragmentDirections.actionWindowFragmentToSearchFragment(),
                FragmentNavigatorExtras(
                    binding.tvTitle to getString(R.string.transition_name_search)
                )
            )
            return true
        }

        override fun onScroll(
            e1: MotionEvent,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            if (!scrolling) {
                startEvent = MotionEvent.obtain(e2)
                onGestureStart?.invoke()
            }
            val dx = e2.x - startEvent!!.x
            val dy = e2.y - startEvent!!.y
            onGestureScroll?.invoke(dx, dy)
            return true
        }
    }

    var onGestureStart: (() -> Unit)? = null
    var onGestureScroll: ((dx: Float, dy: Float) -> Unit)? = null
    var onGestureEnd: (() -> Unit)? = null
}