package per.goweii.android.anole.main

import android.app.Dialog
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import per.goweii.android.anole.R
import per.goweii.android.anole.databinding.DialogMenuBinding
import per.goweii.android.anole.window.WindowFragmentDirections

class MenuDialogFragment : BottomSheetDialogFragment() {
    private val mainViewModel by activityViewModels<MainViewModel>()

    private var _binding: DialogMenuBinding? = null
    private val binding get() = _binding!!

    private var originalDimAmount: Float? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (originalDimAmount == null) {
                    originalDimAmount = dialog.window!!.attributes.dimAmount
                }
                val dimAmount = when {
                    slideOffset <= -1F -> 0F
                    slideOffset >= 0F -> originalDimAmount!!
                    else -> (1F + slideOffset) * originalDimAmount!!
                }
                dialog.window?.setDimAmount(dimAmount)
            }
        })
        dialog.behavior.isFitToContents = true
        dialog.behavior.skipCollapsed = true
        dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        dialog.setOnShowListener {
            val window = dialog.window!!
            WindowCompat.setDecorFitsSystemWindows(window, false)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            val systemUiVisibility = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogMenuRv.apply {
            layoutManager =
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    GridLayoutManager(requireContext(), 6, GridLayoutManager.VERTICAL, false)
                } else {
                    GridLayoutManager(requireContext(), 4, GridLayoutManager.VERTICAL, false)
                }
            adapter = MenuAdapter(buildActions())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        _binding?.dialogMenuRv?.let {
            val lm = it.layoutManager
            if (lm is GridLayoutManager) {
                if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    lm.spanCount = 6
                } else {
                    lm.spanCount = 4
                }
                it.requestLayout()
            }
        }
    }

    private fun buildActions(): List<MenuAction> {
        return arrayListOf(
            MenuAction(getString(R.string.refresh), R.drawable.ic_reload) {
                findNavController().navigateUp()
                mainViewModel.reload()
            },
            MenuAction(getString(R.string.scan_code), R.drawable.ic_scan) {
                findNavController().navigateUp()
                findNavController().navigate(
                    WindowFragmentDirections.actionWindowFragmentToScanFragment()
                )
            },
            MenuAction(getString(R.string.setting), R.drawable.ic_setting) {
                findNavController().navigateUp()
                findNavController().navigate(
                    WindowFragmentDirections.actionWindowFragmentToSettingFragment()
                )
            },
            MenuAction(getString(R.string.exit), R.drawable.ic_exit) {
                findNavController().navigateUp()
                activity?.finish()
            },
        )
    }
}