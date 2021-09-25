package per.goweii.android.anole.web

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentAllWebBinding
import per.goweii.android.anole.utils.WebInstance
import per.goweii.android.anole.utils.WebToken
import per.goweii.android.anole.utils.parentViewModels
import per.goweii.android.anole.window.WindowFragment
import per.goweii.android.anole.window.WindowViewModel
import kotlin.math.abs

class AllWebFragment : BaseFragment() {
    private val windowViewModel by parentViewModels<WindowFragment, WindowViewModel>()
    private val viewModel by viewModels<AllWebViewModel>()

    private var _binding: FragmentAllWebBinding? = null
    private val binding get() = _binding!!

    private var recycleView: RecyclerView? = null

    private lateinit var adapter: AllWebAdapter
    private lateinit var transformer: WebPageTransformer

    private var willChoiceMode: Boolean = false
    val isChoiceMode: Boolean
        get() = binding.vpAllWeb.isUserInputEnabled

    private val willChoiceModeHapticFeedbackRunnable = Runnable {
        binding.vpAllWeb.isHapticFeedbackEnabled = true
        binding.vpAllWeb.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }

    private var currentItem: Int
        get() = viewModel.currentItem
        set(value) {
            viewModel.currentItem = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.addFragmentOnAttachListener { _, fragment ->
            val f = fragment as? WebFragment? ?: return@addFragmentOnAttachListener
            bindGestureForFragment(f)
        }
        WebInstance.getInstance(requireContext()).apply {
            onCreateWindow = { createWeb(it) }
            onCloseWindow = { closeWeb(it) }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAllWebBinding.inflate(inflater, container, false)
        binding.vpAllWeb.isUserInputEnabled = false
        binding.vpAllWeb.offscreenPageLimit = 1
        binding.vpAllWeb.isSaveEnabled = false
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::transformer.isInitialized) {
            transformer = WebPageTransformer(
                requireContext().resources.getFraction(R.fraction.web_page_scale, 1, 1),
                requireContext().resources.getDimension(R.dimen.dimenElevation1),
                requireContext().resources.getDimension(R.dimen.dimenMarginDefault),
                requireContext().resources.getDimension(R.dimen.dimenCornerRadiusBig)
            )
        }
        transformer.viewPager = binding.vpAllWeb
        binding.vpAllWeb.setPageTransformer(transformer)
        replaceToLinearSnapHelper()
        if (!this::adapter.isInitialized) {
            adapter = AllWebAdapter(this)
        }
        binding.vpAllWeb.adapter = adapter
        binding.vpAllWeb.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                currentItem = position
            }
        })
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.switchWebSharedFlow.collect {
                val index = viewModel.indexOf(it)
                binding.vpAllWeb.setCurrentItem(index, true)
                exitChoiceMode()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.webTokenListStateFlow.collect {
                adapter.submitWebTokens(it)
                windowViewModel.windowCountLiveData.apply {
                    postValue(adapter.itemCount)
                }
            }
        }
        if (currentItem in 0 until adapter.itemCount) {
            binding.vpAllWeb.setCurrentItem(currentItem, false)
        }
    }

    override fun onDestroyView() {
        recycleView = null
        transformer.viewPager = null
        binding.vpAllWeb.setPageTransformer(null)
        binding.vpAllWeb.adapter = null
        _binding = null
        super.onDestroyView()
    }

    private fun bindGestureForFragment(fragment: WebFragment) {
        val minFlingVelocity = ViewConfiguration.get(context).scaledMinimumFlingVelocity
        val enterChoiceFaction = 0.5F
        val location = intArrayOf(0, 0)
        fragment.onGesture = { e ->
            binding.vpAllWeb.isUserInputEnabled = true
            recycleView?.apply {
                getLocationOnScreen(location)
                e.setLocation(e.x - location[0], e.y - location[1])
                onTouchEvent(e)
            }
            binding.vpAllWeb.isUserInputEnabled = false
        }
        fragment.onGestureStart = {
            willChoiceMode = false
            binding.vpAllWeb.removeCallbacks(willChoiceModeHapticFeedbackRunnable)
        }
        fragment.onGestureScroll = { _, dy ->
            transformer.faction = -dy / ((binding.vpAllWeb.height * (1F - transformer.scale)) / 2F)
            if (transformer.faction > enterChoiceFaction) {
                if (!willChoiceMode) {
                    willChoiceMode = true
                    binding.vpAllWeb.postDelayed(willChoiceModeHapticFeedbackRunnable, 100)
                }
            } else {
                willChoiceMode = false
                binding.vpAllWeb.removeCallbacks(willChoiceModeHapticFeedbackRunnable)
            }
        }
        fragment.onGestureEnd = { vx, vy ->
            willChoiceMode = false
            binding.vpAllWeb.removeCallbacks(willChoiceModeHapticFeedbackRunnable)
            if (abs(vy) > abs(vx) && vy < -minFlingVelocity) {
                exitChoiceMode()
                windowViewModel.showHome()
            } else {
                if (transformer.faction > enterChoiceFaction) {
                    enterChoiceMode()
                } else {
                    exitChoiceMode()
                }
            }
        }
    }

    private fun replaceToLinearSnapHelper() {
        try {
            val mPagerSnapHelperField = ViewPager2::class.java.getDeclaredField("mPagerSnapHelper")
            mPagerSnapHelperField.isAccessible = true
            val mPagerSnapHelper = mPagerSnapHelperField.get(binding.vpAllWeb) as PagerSnapHelper
            mPagerSnapHelper.attachToRecyclerView(null)
            val mRecyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            mRecyclerViewField.isAccessible = true
            val mRecyclerView = mRecyclerViewField.get(binding.vpAllWeb) as RecyclerView
            mRecyclerViewField.set(binding.vpAllWeb, mRecyclerView)
            AllWebSnapHelper(binding.vpAllWeb).attachToRecyclerView(mRecyclerView)
            mRecyclerView.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            mRecyclerView.isVerticalScrollBarEnabled = false
            mRecyclerView.isHorizontalScrollBarEnabled = false
            recycleView = mRecyclerView
        } catch (ignore: Throwable) {
        }
    }

    fun createWeb(webToken: WebToken) {
        if (isDetached) return
        if (!isAdded) return
        viewModel.addWeb(webToken)
        windowViewModel.windowCountLiveData.apply {
            postValue(adapter.itemCount)
        }
        binding.vpAllWeb.post {
            binding.vpAllWeb.setCurrentItem(adapter.itemCount - 1, true)
        }
    }

    fun closeWeb(kernelId: Int) {
        if (isDetached) return
        if (!isAdded) return
        viewModel.removeWeb(WebToken(null, kernelId = kernelId))
        windowViewModel.windowCountLiveData.apply {
            postValue(adapter.itemCount)
        }
    }

    fun loadOnCurWeb(url: String) {
        if (isDetached) return
        if (!isAdded) return
        val id = adapter.getItemId(binding.vpAllWeb.currentItem)
        childFragmentManager.findFragmentByTag("f$id")
            ?.let {
                if (it is WebFragment) {
                    it.loadUrl(url)
                }
            }
    }

    fun enterChoiceMode() {
        if (!::transformer.isInitialized) return
        ValueAnimator.ofFloat(transformer.faction, 1F).apply {
            addUpdateListener {
                transformer.faction = it.animatedValue as Float
            }
        }.start()
    }

    fun exitChoiceMode() {
        if (!::transformer.isInitialized) return
        ValueAnimator.ofFloat(transformer.faction, 0F).apply {
            addUpdateListener {
                transformer.faction = it.animatedValue as Float
            }
        }.start()
    }
}