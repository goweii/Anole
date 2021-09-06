package per.goweii.android.anole.web

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentAllWebBinding
import per.goweii.android.anole.utils.parentViewModelsByAndroid
import per.goweii.android.anole.utils.viewModelsByAndroid
import per.goweii.android.anole.window.WindowFragment
import per.goweii.android.anole.window.WindowViewModel

class AllWebFragment : BaseFragment() {
    private val windowViewModel by parentViewModelsByAndroid<WindowViewModel, WindowFragment>()
    private val viewModel by viewModelsByAndroid<AllWebViewModel>()
    private lateinit var binding: FragmentAllWebBinding
    private lateinit var adapter: AllWebAdapter
    private lateinit var transformer: WebPageTransformer

    val isChoiceMode: Boolean
        get() = binding.vpAllWeb.isUserInputEnabled

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAllWebBinding.inflate(inflater, container, false)
        binding.vpAllWeb.isUserInputEnabled = false
        binding.vpAllWeb.offscreenPageLimit = 1
        binding.vpAllWeb.isSaveEnabled = false
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!this::transformer.isInitialized) {
            transformer = WebPageTransformer(
                binding.vpAllWeb,
                requireContext().resources.getFraction(R.fraction.web_page_scale, 1, 1),
                requireContext().resources.getDimension(R.dimen.dimenElevation1),
                requireContext().resources.getDimension(R.dimen.dimenMarginDefault),
                requireContext().resources.getDimension(R.dimen.dimenCornerRadiusBig)
            )
        }
        binding.vpAllWeb.setPageTransformer(transformer)
        if (!this::adapter.isInitialized) {
            adapter = AllWebAdapter(this)
        }
        binding.vpAllWeb.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.onTouchSharedFlow.collect {
                val index = adapter.indexOf(it)
                binding.vpAllWeb.setCurrentItem(index, true)
                exitChoiceMode()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.onRemoveSharedFlow.collect {
                adapter.removeWeb(it)
                adapter.notifyDataSetChanged()
                windowViewModel.windowCountLiveData.apply {
                    postValue(adapter.itemCount)
                }
            }
        }
    }

    override fun onDestroyView() {
        binding.vpAllWeb.setPageTransformer(null)
        binding.vpAllWeb.adapter = null
        super.onDestroyView()
    }

    fun createNewWeb(initialUrl: String?) {
        if (isDetached) return
        if (!isAdded) return
        adapter.addWeb(WebFragment.newInstance(initialUrl))
        binding.vpAllWeb.setCurrentItem(adapter.itemCount - 1, true)
        windowViewModel.windowCountLiveData.apply {
            postValue(adapter.itemCount)
        }
    }

    fun enterChoiceMode() {
        ValueAnimator.ofFloat(transformer.faction, 1F).apply {
            doOnEnd {
                binding.vpAllWeb.isUserInputEnabled = true
            }
            addUpdateListener {
                transformer.faction = it.animatedValue as Float
                binding.vpAllWeb.requestTransform()
            }
        }.start()
    }

    fun exitChoiceMode() {
        ValueAnimator.ofFloat(transformer.faction, 0F).apply {
            doOnStart {
                binding.vpAllWeb.isUserInputEnabled = false
            }
            addUpdateListener {
                transformer.faction = it.animatedValue as Float
                binding.vpAllWeb.requestTransform()
            }
        }.start()
    }
}