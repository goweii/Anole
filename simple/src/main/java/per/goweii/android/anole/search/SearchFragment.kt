package per.goweii.android.anole.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.doOnAttach
import androidx.core.widget.addTextChangedListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentSearchBinding
import per.goweii.android.anole.main.MainViewModel
import per.goweii.android.anole.utils.DefSearch
import per.goweii.android.anole.utils.Url
import per.goweii.android.anole.utils.activityViewModelsByAndroid

class SearchFragment : BaseFragment() {
    private val mainViewModel by activityViewModelsByAndroid<MainViewModel>()
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

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
        view.doOnAttach {
            binding.vStatusBar.layoutParams.height = ViewCompat.getRootWindowInsets(view)
                ?.getInsets(WindowInsetsCompat.Type.systemBars())?.top ?: 0
        }
        binding.ivBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
        binding.etSearch.apply {
            imeOptions = EditorInfo.IME_ACTION_GO
            addTextChangedListener {
                val url = Url.parse(it?.toString())
                if (url.maybeUrl) {
                    binding.ivSearch.setImageResource(R.drawable.ic_enter)
                } else {
                    binding.ivSearch.setImageResource(R.drawable.ic_search)
                }
            }
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_GO -> {
                        doSearch()
                    }
                }
                return@setOnEditorActionListener true
            }
        }
        binding.ivSearch.setOnClickListener {
            doSearch()
        }
        binding.etSearch.post {
            binding.etSearch.requestFocus()
            val imm = requireContext().getSystemService<InputMethodManager>()!!
            imm.showSoftInput(binding.etSearch, 0)
        }
    }

    override fun onDestroyView() {
        val imm = requireContext().getSystemService<InputMethodManager>()!!
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
        _binding = null
        super.onDestroyView()
    }

    private fun doSearch() {
        val text = binding.etSearch.text?.toString() ?: return
        binding.etSearch.clearFocus()
        val url = Url.parse(text).toUrl()
            ?: DefSearch.getInstance(requireContext()).getDefSearch().getSearchUrl(text)
        mainViewModel.loadUrlFromSearch = url
        findNavController().navigateUp()
    }
}