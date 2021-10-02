package per.goweii.android.anole.window

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
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
import per.goweii.android.anole.utils.UrlLoadEntry
import per.goweii.android.anole.utils.WebToken
import per.goweii.android.anole.utils.ext.listener
import per.goweii.android.anole.web.AllWebFragment

class WindowFragment : BaseFragment() {
    private val viewModel by viewModels<WindowViewModel>()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.switchChoiceModeSharedFlow.collect {
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
            viewModel.homeStateFlow.collect {
                if (it) {
                    showHomeFragment()
                } else {
                    showAllWebFragment()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newWindowSharedFlow.collect {
                allWebFragment.createWeb(WebToken(it))
            }
        }
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<UrlLoadEntry?>("new_url")
            ?.apply {
                observe(viewLifecycleOwner) {
                    if (it != null) {
                        viewModel.showWeb()
                        binding.root.post {
                            if (it.newWindow) {
                                allWebFragment.createWeb(WebToken(it.url))
                            } else {
                                allWebFragment.loadOnCurWeb(it.url)
                            }
                        }
                        value = null
                    }
                }
            }
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            val args: WindowFragmentArgs by navArgs()
            if (!args.initialUrl.isNullOrBlank()) {
                viewModel.newWindow(args.initialUrl)
            }
        }
    }

    private fun showHomeFragment() {
        if (homeFragment.isVisible) return

        fun runHideWebAnim(finish: () -> Unit) {
            if (!allWebFragment.isVisible) {
                finish.invoke()
                return
            }
            val view = allWebFragment.view
            if (view == null) {
                finish.invoke()
                return
            }
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.alpha_exit)
            view.startAnimation(anim)
            anim.listener { onEnd = { finish.invoke() } }
        }

        fun runShowHomeAnim() {
            val view = homeFragment.view
            view?.doOnPreDraw {
                val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_small_enter)
                view.startAnimation(anim)
            }
        }

        runHideWebAnim {
            allWebFragment.exitChoiceMode()
            childFragmentManager.commit {
                hide(allWebFragment)
                show(homeFragment)
                setMaxLifecycle(allWebFragment, Lifecycle.State.STARTED)
                setMaxLifecycle(homeFragment, Lifecycle.State.RESUMED)
                runShowHomeAnim()
            }
        }
    }

    private fun showAllWebFragment() {
        if (allWebFragment.isVisible) return

        fun runHideHomeAnim(finish: () -> Unit) {
            if (!homeFragment.isVisible) {
                finish.invoke()
                return
            }
            val view = homeFragment.view
            if (view == null) {
                finish.invoke()
                return
            }
            val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.alpha_exit)
            view.startAnimation(anim)
            anim.listener { onEnd = { finish.invoke() } }
        }

        fun runShowWebAnim() {
            val view = allWebFragment.view
            view?.doOnPreDraw {
                val anim = AnimationUtils.loadAnimation(requireContext(), R.anim.zoom_big_enter)
                view.startAnimation(anim)
            }
        }

        runHideHomeAnim {
            childFragmentManager.commit {
                hide(homeFragment)
                show(allWebFragment)
                setMaxLifecycle(homeFragment, Lifecycle.State.STARTED)
                setMaxLifecycle(allWebFragment, Lifecycle.State.RESUMED)
                runShowWebAnim()
            }
        }
    }

    private fun loadUrlOnNewWeb(url: String?) {
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

}