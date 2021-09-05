package per.goweii.android.anole.window

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentWindowBinding
import per.goweii.android.anole.home.HomeFragment
import per.goweii.android.anole.main.MainViewModel
import per.goweii.android.anole.utils.DefHome
import per.goweii.android.anole.utils.activityViewModelsByAndroid
import per.goweii.android.anole.utils.viewModelsByAndroid
import per.goweii.android.anole.web.AllWebFragment

class WindowFragment : BaseFragment() {
    private val mainViewModel by activityViewModelsByAndroid<MainViewModel>()
    private val viewModel by viewModelsByAndroid<WindowViewModel>()
    private lateinit var binding: FragmentWindowBinding

    private lateinit var homeFragment: HomeFragment
    private lateinit var allWebFragment: AllWebFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentWindowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareFragments()
        viewModel.windowCountLiveData.observe(viewLifecycleOwner) {
            if (it > 0) {
                binding.tvWindowsCount.text = it.toString()
            } else {
                binding.tvWindowsCount.text = getString(R.string.add_window)
                showHomeFragment()
                allWebFragment.exitChoiceMode()
            }
        }
        viewModel.goBackEnableLiveData.observe(viewLifecycleOwner) {
            binding.ivBack.isEnabled = it
            binding.ivBack.animate().alpha(if (it) 1F else 0.6F).start()
        }
        viewModel.goForwardEnableLiveData.observe(viewLifecycleOwner) {
            binding.ivForward.isEnabled = it
            binding.ivForward.animate().alpha(if (it) 1F else 0.6F).start()
        }
        binding.ivBack.setOnClickListener {
            viewModel.getBackOrForward(-1)
        }
        binding.ivForward.setOnClickListener {
            viewModel.getBackOrForward(1)
        }
        binding.ivMenu.setOnClickListener {
            showMenuDialog()
        }
        binding.ivHome.setOnClickListener {
            showHomeFragment()
            allWebFragment.exitChoiceMode()
        }
        binding.rlWindows.setOnClickListener {
            val windowCount = (viewModel.windowCountLiveData.value ?: 0).coerceAtLeast(0)
            if (windowCount == 0) {
                showAllWebFragment()
                allWebFragment.createNewWeb(DefHome.getInstance(requireContext()).getDefHome())
            } else {
                if (!allWebFragment.isVisible) {
                    showAllWebFragment()
                } else {
                    if (allWebFragment.isChoiceMode) {
                        allWebFragment.exitChoiceMode()
                    } else {
                        allWebFragment.enterChoiceMode()
                    }
                }
            }
        }
        binding.rlWindows.setOnLongClickListener {
            showAllWebFragment()
            allWebFragment.createNewWeb(DefHome.getInstance(requireContext()).getDefHome())
            return@setOnLongClickListener true
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadUrlOnNewWindowSharedFlow.collect {
                loadUrlOnNewWeb(it)
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val args: WindowFragmentArgs by navArgs()
            if (!args.initialUrl.isNullOrBlank()) {
                showAllWebFragment()
                allWebFragment.createNewWeb(args.initialUrl)
            } else {
                showHomeFragment()
                allWebFragment.exitChoiceMode()
            }
        }
    }

    private fun prepareFragments() {
        childFragmentManager.commit {
            homeFragment = childFragmentManager
                .findFragmentByTag(HomeFragment::class.java.name) as HomeFragment?
                ?: HomeFragment().also {
                    add(R.id.fragment_container_view, it, HomeFragment::class.java.name)
                }
            allWebFragment = childFragmentManager
                .findFragmentByTag(AllWebFragment::class.java.name) as AllWebFragment?
                ?: AllWebFragment().also {
                    add(R.id.fragment_container_view, it, AllWebFragment::class.java.name)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        mainViewModel.loadUrlFromSearch?.let {
            allWebFragment.createNewWeb(it)
            showAllWebFragment()
            mainViewModel.loadUrlFromSearch = null
        }
    }

    private fun showHomeFragment() {
        if (homeFragment.isVisible) return
        childFragmentManager.commit {
            hide(allWebFragment)
            show(homeFragment)
            setMaxLifecycle(allWebFragment, Lifecycle.State.STARTED)
            setMaxLifecycle(homeFragment, Lifecycle.State.RESUMED)
            homeFragment.view?.doOnPreDraw {
                val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.small_zoom_enter)
                it.startAnimation(anim)
            }
        }
    }

    private fun showAllWebFragment() {
        if (allWebFragment.isVisible) return
        childFragmentManager.commit {
            hide(homeFragment)
            show(allWebFragment)
            setMaxLifecycle(homeFragment, Lifecycle.State.STARTED)
            setMaxLifecycle(allWebFragment, Lifecycle.State.RESUMED)
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.small_zoom_enter)
            allWebFragment.view?.doOnPreDraw {
                it.startAnimation(anim)
            }
        }
    }

    private fun loadUrlOnNewWeb(url: String?) {
        showAllWebFragment()
        allWebFragment.createNewWeb(url ?: getString(R.string.initial_url))
    }

    private fun showMenuDialog() {
        findNavController().navigate(
            WindowFragmentDirections.actionWindowFragmentToMenuDialogFragment()
        )
    }

}