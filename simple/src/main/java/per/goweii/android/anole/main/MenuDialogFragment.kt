package per.goweii.android.anole.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import per.goweii.android.anole.R
import per.goweii.android.anole.databinding.DialogMenuBinding
import per.goweii.android.anole.utils.activityViewModelsByAndroid
import per.goweii.android.anole.window.WindowFragmentDirections

class MenuDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: DialogMenuBinding
    private val mainViewModel by activityViewModelsByAndroid<MainViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val list = arrayListOf<MenuAction>()
        list.add(MenuAction("刷新", R.drawable.ic_reload) {
            findNavController().navigateUp()
            mainViewModel.reload()
        })
        list.add(MenuAction("扫码", R.drawable.ic_scan) {
            findNavController().navigateUp()
            findNavController().navigate(
                WindowFragmentDirections.actionWindowFragmentToScanFragment()
            )
        })
        binding.dialogMenuRv.apply {
            layoutManager = GridLayoutManager(context, 4, GridLayoutManager.VERTICAL, false)
            adapter = MenuAdapter(list)
        }
    }
}