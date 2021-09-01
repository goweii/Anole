package per.goweii.android.anole.window

import android.content.Intent
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentWindowBinding
import per.goweii.android.anole.home.HomeFragment
import per.goweii.android.anole.main.MenuAction
import per.goweii.android.anole.main.MenuAdapter
import per.goweii.android.anole.scan.ScanActivity
import per.goweii.android.anole.utils.DefHome
import per.goweii.android.anole.utils.DefSearch
import per.goweii.android.anole.utils.viewModelsByAndroid
import per.goweii.android.anole.web.AllWebFragment
import per.goweii.layer.core.anim.AnimStyle
import per.goweii.layer.core.widget.SwipeLayout
import per.goweii.layer.dialog.DialogLayer

class WindowFragment : BaseFragment() {
    private val viewModel: WindowViewModel by viewModelsByAndroid()
    private val args: WindowFragmentArgs by navArgs()
    private lateinit var binding: FragmentWindowBinding

    private lateinit var homeFragment: HomeFragment
    private lateinit var allWebFragment: AllWebFragment

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (!this::binding.isInitialized) {
            binding = FragmentWindowBinding.inflate(inflater, container, false)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        homeFragment = childFragmentManager
            .findFragmentByTag(HomeFragment::class.java.name) as HomeFragment?
            ?: HomeFragment()
        allWebFragment = childFragmentManager
            .findFragmentByTag(AllWebFragment::class.java.name) as AllWebFragment?
            ?: AllWebFragment()
        childFragmentManager.commit {
            add(R.id.fragment_container_view, homeFragment)
            add(R.id.fragment_container_view, allWebFragment)
        }
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            if (!args.initialUrl.isNullOrBlank()) {
                showAllWebFragment()
                allWebFragment.createNewWeb(args.initialUrl)
            } else {
                showHomeFragment()
                allWebFragment.exitChoiceMode()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loadUrlOnNewWindowSharedFlow.collect {
                loadUrlOnNewWeb(it)
            }
        }
    }

    private fun searchKeyword(keyword: String) {
        showAllWebFragment()
        allWebFragment.createNewWeb(
            DefSearch.getInstance(requireContext())
                .getDefSearch()
                .getSearchUrl(keyword)
        )
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
        DialogLayer(requireActivity())
            .setBackgroundDimDefault()
            .setContentView(R.layout.dialog_menu)
            .setContentAnimator(AnimStyle.BOTTOM)
            .setSwipeDismiss(SwipeLayout.Direction.BOTTOM)
            .addOnBindDataListener { layer ->
                val rv = layer.requireView<RecyclerView>(R.id.dialog_menu_rv)
                val list = arrayListOf<MenuAction>()
                list.add(MenuAction("收藏", R.drawable.ic_bookmark) {
                    layer.dismiss()
                })
                list.add(MenuAction("刷新", R.drawable.ic_reload) {
                    layer.dismiss()
                })
                list.add(MenuAction("扫码", R.drawable.ic_scan) {
                    layer.dismiss()
                    startActivity(Intent(context, ScanActivity::class.java))
                })
                rv.apply {
                    layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
                    adapter = MenuAdapter(list)
                }
            }
            .show()
    }

}