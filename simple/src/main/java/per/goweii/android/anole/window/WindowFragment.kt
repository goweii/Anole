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
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentWindowBinding
import per.goweii.android.anole.home.HomeFragment
import per.goweii.android.anole.main.MainViewModel
import per.goweii.android.anole.utils.activityViewModelsByAndroid
import per.goweii.android.anole.utils.viewModelsByAndroid
import per.goweii.android.anole.web.AllWebFragment

class WindowFragment : BaseFragment() {
    private val mainViewModel by activityViewModelsByAndroid<MainViewModel>()
    private val viewModel by viewModelsByAndroid<WindowViewModel>()
    private lateinit var binding: FragmentWindowBinding

    private lateinit var homeFragment: HomeFragment
    private lateinit var allWebFragment: AllWebFragment

    private var currentFragment: String? = null

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.switchChoiceModeSharedFlow.collect {
                showAllWebFragment()
                if (it == true) {
                    allWebFragment.enterChoiceMode()
                } else if (it == false) {
                    allWebFragment.exitChoiceMode()
                } else {
                    if (allWebFragment.isChoiceMode) {
                        allWebFragment.exitChoiceMode()
                    } else {
                        allWebFragment.enterChoiceMode()
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showHomeModeSharedFlow.collect {
                showHomeFragment()
                allWebFragment.exitChoiceMode()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.showWebModeSharedFlow.collect {
                showAllWebFragment()
                if (it) {
                    allWebFragment.enterChoiceMode()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadUrlOnNewWindowSharedFlow.collect {
                loadUrlOnNewWeb(it)
            }
        }
        when {
            HomeFragment::class.java.name == currentFragment -> {
                showHomeFragment()
            }
            AllWebFragment::class.java.name == currentFragment -> {
                showAllWebFragment()
            }
            else -> {
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
        }
    }

    private fun prepareFragments() {
        childFragmentManager.commit {
            homeFragment = childFragmentManager
                .findFragmentByTag(HomeFragment::class.java.name) as HomeFragment?
                ?: HomeFragment().also {
                    add(R.id.fragment_container_view, it, HomeFragment::class.java.name)
                }
            hide(homeFragment)
            allWebFragment = childFragmentManager
                .findFragmentByTag(AllWebFragment::class.java.name) as AllWebFragment?
                ?: AllWebFragment().also {
                    add(R.id.fragment_container_view, it, AllWebFragment::class.java.name)
                }
            hide(allWebFragment)
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

    override fun onDestroyView() {
        super.onDestroyView()
        if (allWebFragment.isVisible) {
            currentFragment = AllWebFragment::class.java.name
        } else if (homeFragment.isVisible) {
            currentFragment = HomeFragment::class.java.name
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

}