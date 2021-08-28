package per.goweii.android.anole.utils

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import per.goweii.android.anole.AnoleApplication

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.viewModelsByAndroid() =
    viewModels<VM> {
        ViewModelProvider.AndroidViewModelFactory(AnoleApplication.application)
    }

@MainThread
inline fun <reified VM : ViewModel> Fragment.viewModelsByAndroid(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this }
) = viewModels<VM>(ownerProducer) {
    ViewModelProvider.AndroidViewModelFactory(AnoleApplication.application)
}

@MainThread
inline fun <reified VM : ViewModel> Fragment.activityViewModelsByAndroid() =
    activityViewModels<VM> {
        ViewModelProvider.AndroidViewModelFactory(AnoleApplication.application)
    }