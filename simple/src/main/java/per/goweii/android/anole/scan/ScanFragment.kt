package per.goweii.android.anole.scan

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import per.goweii.android.anole.base.BaseFragment
import per.goweii.android.anole.databinding.FragmentScanBinding
import per.goweii.android.anole.main.MainViewModel
import per.goweii.android.anole.utils.DefSearch
import per.goweii.android.anole.utils.Url
import per.goweii.android.anole.utils.UrlLoadEntry
import per.goweii.codex.processor.zxing.ZXingScanProcessor

@SuppressLint("MissingPermission")
class ScanFragment : BaseFragment() {
    companion object {
        private const val REQ_CODE_CAMERA = 1001
    }

    private val mainViewModel by activityViewModels<MainViewModel>()
    private lateinit var binding: FragmentScanBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentScanBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.ivClose.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.codeScanner.apply {
            addProcessor(ZXingScanProcessor())
            addDecorator(binding.finderView)
            onFound {
                val text = it.first().text
                val url = Url.parse(text).toUrl() ?: DefSearch.getInstance(requireContext())
                    .getDefSearch().getSearchUrl(text)
                findNavController().apply {
                    previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("new_url", UrlLoadEntry(url, true))
                    popBackStack()
                }
            }
            bindToLifecycle(this@ScanFragment.viewLifecycleOwner)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                binding.codeScanner.startScan()
            } else {
                requestPermissions(arrayOf(Manifest.permission.CAMERA), REQ_CODE_CAMERA)
            }
        } else {
            binding.codeScanner.startScan()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ_CODE_CAMERA -> {
                if (grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    binding.codeScanner.startScan()
                } else {
                    findNavController().navigateUp()
                }
            }
        }
    }
}