package per.goweii.android.anole.main

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import per.goweii.android.anole.NavGraphDirections
import per.goweii.android.anole.R
import per.goweii.android.anole.databinding.ActivityMainBinding
import per.goweii.android.anole.databinding.DialogMenuBinding
import per.goweii.android.anole.scan.ScanActivity
import per.goweii.android.anole.utils.DefSearch
import per.goweii.android.anole.utils.Url
import per.goweii.android.anole.utils.viewModelsByAndroid
import per.goweii.android.anole.web.WebFragment
import per.goweii.layer.core.Layer
import per.goweii.layer.core.anim.CommonAnimatorCreator
import per.goweii.layer.popup.PopupLayer
import per.goweii.layer.visualeffectview.PopupShadowLayout

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModelsByAndroid()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel.windowCountLiveData.observe(this) {
            binding.tvWindowsCount.text = if (it > 0) {
                it.toString()
            } else {
                getString(R.string.add_window)
            }
        }
        viewModel.goBackEnableLiveData.observe(this) {
            binding.ivBack.isEnabled = it
            binding.ivBack.animate().alpha(if (it) 1F else 0.6F).start()
        }
        viewModel.goForwardEnableLiveData.observe(this) {
            binding.ivForward.isEnabled = it
            binding.ivForward.animate().alpha(if (it) 1F else 0.6F).start()
        }
        viewModel.currUrlLiveData.observe(this) {
            binding.urlInputView.setUrl(it)
        }
        viewModel.currTitleLiveData.observe(this) {
            binding.urlInputView.setTitle(it)
        }
        viewModel.currIconLiveData.observe(this) {
            binding.urlInputView.setIcon(it)
        }
        viewModel.progressLiveData.observe(this) {
            if (binding.pb.max != 100) {
                binding.pb.max = 100
            }
            if (it in 0..100) {
                ObjectAnimator.ofInt(binding.pb, "progress", binding.pb.progress, it).start()
                binding.pb.animate().alpha(1F).start()
            } else {
                binding.pb.progress = 0
                binding.pb.animate().alpha(0F).start()
            }
        }
        binding.ivBack.setOnClickListener {
            viewModel.goBackOrForwardLiveData.postValue(-1)
        }
        binding.ivForward.setOnClickListener {
            viewModel.goBackOrForwardLiveData.postValue(1)
        }
        binding.ivMenu.setOnClickListener {
            showMenuDialog()
        }
        binding.ivHome.setOnClickListener {
            binding.navHostFragment.findNavController().navigate(R.id.homeFragment)
        }
        binding.rlWindows.setOnClickListener {
            val windowCount = (viewModel.windowCountLiveData.value ?: 0).coerceAtLeast(0)
            if (windowCount == 0) {
                binding.navHostFragment.findNavController()
                    .navigate(NavGraphDirections.actionWebFragment(getHomeUrl()))
            } else {
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                    ?.childFragmentManager
                    ?.fragments
                    ?.filterIsInstance<WebFragment>()
                    ?.let {

                    }
            }
        }
        binding.rlWindows.setOnLongClickListener {
            binding.navHostFragment.findNavController()
                .navigate(NavGraphDirections.actionWebFragment(getHomeUrl()))
            return@setOnLongClickListener true
        }
        binding.urlInputView.apply {
            onCollect = { viewModel.reloadLiveData.postValue(true) }
            onEnter = { viewModel.loadUrlLiveData.postValue(it) }
            onSearch = {
                binding.navHostFragment.findNavController()
                    .currentBackStackEntry
                    ?.apply {
                        if (destination.id == R.id.homeFragment) {
                            binding.navHostFragment.findNavController()
                                .navigate(NavGraphDirections.actionWebFragment(getSearchUrl(it)))
                        } else {
                            viewModel.loadUrlLiveData.postValue(getSearchUrl(it))
                        }
                    }
            }
            onDefSearch = { ivIcon ->
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
                            DefSearch.getInstance(this@MainActivity).getAllSearch()
                        ).apply {
                            onChoice = {
                                DefSearch.getInstance(this@MainActivity).saveDefSearch(it)
                                layer.dismiss()
                            }
                        }
                    }
                    .show()
            }
        }
        intent?.let { parseIntentToOpenUrl(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { parseIntentToOpenUrl(it) }
    }

    private fun parseIntentToOpenUrl(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            val url = Url.parse(intent.data?.toString())
            val link = url.toUrl()
            if (link.isNullOrBlank()) {
                binding.navHostFragment.findNavController()
                    .navigate(NavGraphDirections.actionWebFragment(getSearchUrl(url.toString())))
            } else {
                binding.navHostFragment.findNavController()
                    .navigate(NavGraphDirections.actionWebFragment(link))
            }
        }
    }

    private fun getHomeUrl(): String {
        return "https://www.baidu.com"
    }

    private fun getSearchUrl(str: String): String {
        return DefSearch.getInstance(this)
            .getDefSearch()
            .getSearchUrl(str)
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            if (binding.urlInputView.hasFocus()) {
                val etLocation = IntArray(2)
                binding.urlInputView.getLocationInWindow(etLocation)
                if (ev.rawX.toInt() !in (etLocation[0])..(etLocation[0] + binding.urlInputView.width) ||
                    ev.rawY.toInt() !in (etLocation[1])..(etLocation[1] + binding.urlInputView.height)
                ) {
                    binding.urlInputView.clearFocus()
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }

    private fun showMenuDialog() {
        BottomSheetDialog(this).apply {
            val list = arrayListOf<MenuAction>()
            list.add(MenuAction("收藏", R.drawable.ic_bookmark) {
                dismiss()
            })
            list.add(MenuAction("刷新", R.drawable.ic_reload) {
                dismiss()
            })
            list.add(MenuAction("扫码", R.drawable.ic_scan) {
                dismiss()
                startActivity(Intent(this@MainActivity, ScanActivity::class.java))
            })
            list.add(MenuAction("退出", R.drawable.ic_finish) {
                dismiss()
                finish()
            })
            val binding = DialogMenuBinding.inflate(layoutInflater)
            binding.rvMenu.apply {
                layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
                adapter = MenuAdapter(list)
            }
            binding.ivClose.setOnClickListener {
                dismiss()
            }
            setContentView(
                binding.root, ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            )
        }.show()
    }
}