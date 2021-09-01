package per.goweii.android.anole.main

import android.content.Intent
import android.os.Bundle
import androidx.navigation.findNavController
import per.goweii.android.anole.NavGraphMainDirections
import per.goweii.android.anole.base.BaseActivity
import per.goweii.android.anole.databinding.ActivityMainBinding
import per.goweii.android.anole.utils.viewModelsByAndroid

class MainActivity : BaseActivity() {
    private val viewModel: MainViewModel by viewModelsByAndroid()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let { parseIntentToOpenUrl(it) }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let { parseIntentToOpenUrl(it) }
    }

    private fun parseIntentToOpenUrl(intent: Intent) {
        if (intent.action == Intent.ACTION_VIEW) {
            binding.navHostFragmentMain.findNavController()
                .navigate(NavGraphMainDirections.actionGlobalWindowFragment(intent.data?.toString()))
        }
    }
}