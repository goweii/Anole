package per.goweii.android.anole.settings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import per.goweii.android.anole.R
import per.goweii.android.anole.databinding.DialogSelectDefSearchBinding
import per.goweii.android.anole.utils.DefSearch
import per.goweii.android.anole.utils.parentViewModels

class SelectDefSearchDialogFragment : BottomSheetDialogFragment() {
    private val settingViewModel by parentViewModels<SettingFragment, SettingViewModel>()
    private var _binding: DialogSelectDefSearchBinding? = null
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
        _binding = DialogSelectDefSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.dialogMenuRv.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = SelectDefSearchAdapter(
                DefSearch.getInstance(requireContext()).getAllSearch()
            ).apply {
                onItemClick = {
                    DefSearch.getInstance(requireContext()).saveDefSearch(it)
                    findNavController().previousBackStackEntry
                        ?.savedStateHandle
                        ?.getLiveData<DefSearch.CustomSearch>("def_search")
                        ?.value = it
                    findNavController().navigateUp()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}