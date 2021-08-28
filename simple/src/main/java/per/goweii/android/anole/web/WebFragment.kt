package per.goweii.android.anole.web

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import per.goweii.android.anole.R
import per.goweii.android.anole.databinding.FragmentWebBinding
import per.goweii.android.anole.main.MainViewModel
import per.goweii.android.anole.utils.activityViewModelsByAndroid
import per.goweii.anole.WebFactory
import per.goweii.anole.ability.impl.BackForwardIconAbility
import per.goweii.anole.ability.impl.PageInfoAbility
import per.goweii.anole.ability.impl.ProgressAbility
import per.goweii.anole.view.KernelView

class WebFragment : Fragment() {
    private val mainViewModel: MainViewModel by activityViewModelsByAndroid()

    private val args: WebFragmentArgs by navArgs()
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
        binding = FragmentWebBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        kernelView = createWeb(args.initialUrl ?: getString(R.string.initial_url))
        backForwardIconAbility = BackForwardIconAbility(
            canGoBack = { mainViewModel.goBackEnableLiveData.postValue(it) },
            canGoForward = { mainViewModel.goForwardEnableLiveData.postValue(it) }
        )
        progressAbility = ProgressAbility(
            onProgress = { mainViewModel.progressLiveData.postValue(it) }
        )
        pageInfoAbility = PageInfoAbility(
            onReceivedPageUrl = { mainViewModel.currUrlLiveData.postValue(it) },
            onReceivedPageTitle = { mainViewModel.currTitleLiveData.postValue(it) },
            onReceivedPageIcon = { mainViewModel.currIconLiveData.postValue(it) }
        )
        mainViewModel.goBackOrForwardLiveData.observe(viewLifecycleOwner) { step ->
            kernelView.goBackOrForward(step)
        }
        mainViewModel.windowCountLiveData.apply {
            postValue(((value ?: 0) + 1).coerceAtLeast(0))
        }
    }

    override fun onResume() {
        super.onResume()
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mainViewModel.windowCountLiveData.apply {
            postValue(((value ?: 0) - 1).coerceAtLeast(0))
        }
    }

    private fun createWeb(url: String): KernelView {
        val kernelView = WebFactory.with(requireActivity())
            .applyDefaultConfig()
            .attachTo(binding.webContainer)
            .bindToLifecycle(viewLifecycleOwner)
            .get()
        kernelView.loadUrl(url)
        return kernelView
    }
}