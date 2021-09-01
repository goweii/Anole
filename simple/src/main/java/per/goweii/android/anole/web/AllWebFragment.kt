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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!::binding.isInitialized) {
            binding = FragmentAllWebBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vpAllWeb.isUserInputEnabled = false
        transformer = WebPageTransformer(
            binding.vpAllWeb,
            0.6F,
            requireContext().resources.getDimension(R.dimen.dimenElevation1),
            requireContext().resources.getDimension(R.dimen.dimenMarginDefault),
            requireContext().resources.getDimension(R.dimen.dimenCornerRadiusDefault)
        )
        binding.vpAllWeb.setPageTransformer(transformer)
        adapter = AllWebAdapter(this)
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
                windowViewModel.windowCountLiveData.apply {
                    postValue(adapter.itemCount)
                }
            }
        }
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

    fun closeCurrWeb() {
        if (isDetached) return
        if (!isAdded) return
        adapter.removeWebAt(binding.vpAllWeb.currentItem)
        windowViewModel.windowCountLiveData.apply {
            postValue(adapter.itemCount)
        }
    }

    val isChoiceMode: Boolean
        get() = binding.vpAllWeb.isUserInputEnabled

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