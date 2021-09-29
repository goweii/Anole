package per.goweii.android.anole.settings

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentSettingBinding
import per.goweii.android.anole.utils.DefSearch

class SettingFragment : BaseFragment() {
    private val viewModel by viewModels<SettingViewModel>()
    private var _binding: FragmentSettingBinding? = null
    private val binding: FragmentSettingBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.defSearchFlow.collect {
                binding.tvDefSearch.text = it.name ?: Uri.parse(it.url).host
            }
        }
        findNavController()
            .currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<DefSearch.CustomSearch>("def_search")
            ?.observe(viewLifecycleOwner) {
                viewModel.reloadDefSearch()
            }
        binding.iconBack.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.rlDefSearch.setOnClickListener {
            findNavController().navigate(
                SettingFragmentDirections.actionSettingFragmentToSelectDefSearchDialogFragment()
            )
        }
    }

}