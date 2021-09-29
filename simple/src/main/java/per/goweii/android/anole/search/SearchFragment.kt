package per.goweii.android.anole.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentSearchBinding
import per.goweii.android.anole.utils.UrlLoadEntry

class SearchFragment : BaseFragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val args: SearchFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.transition_auto)
        sharedElementReturnTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.transition_auto)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.urlInputView.onEnter = {
            findNavController().apply {
                previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("new_url", UrlLoadEntry(it, !args.fromWindow))
                popBackStack()
            }
        }
        binding.urlInputView.setText(args.url)
        binding.urlInputView.requestFocusAndShowIm()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}