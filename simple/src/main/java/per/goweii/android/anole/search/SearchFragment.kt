package per.goweii.android.anole.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.widget.addTextChangedListener
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import per.goweii.android.anole.R
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentSearchBinding
import per.goweii.android.anole.utils.DefSearch
import per.goweii.android.anole.utils.Url

class SearchFragment : BaseFragment() {
    private var binding: FragmentSearchBinding? = null

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
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.ivBack?.setOnClickListener {
            it.findNavController().navigateUp()
        }
        binding?.etSearch?.apply {
            imeOptions = EditorInfo.IME_ACTION_GO
            addTextChangedListener {
                val url = Url.parse(it?.toString())
                if (url.maybeUrl) {
                    binding?.ivSearch?.setImageResource(R.drawable.ic_enter)
                } else {
                    binding?.ivSearch?.setImageResource(R.drawable.ic_search)
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
        binding?.ivSearch?.setOnClickListener {
            doSearch()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    private fun doSearch() {
        val et = binding?.etSearch ?: return
        val text = et.text?.toString() ?: return
        et.clearFocus()
        val url = Url.parse(text).toUrl()
            ?: DefSearch.getInstance(requireContext()).getDefSearch().getSearchUrl(text)
        findNavController().navigate(
            SearchFragmentDirections.actionSearchFragmentToWindowFragment(url)
        )
    }
}