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

val ComponentActivity.androidViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    get() = ViewModelProvider.AndroidViewModelFactory.getInstance(application)

val Fragment.androidViewModelFactory: ViewModelProvider.AndroidViewModelFactory
    get() = ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)

@MainThread
inline fun <reified VM : ViewModel> ComponentActivity.viewModelsByAndroid() =
    viewModels<VM> { androidViewModelFactory }

@MainThread
inline fun <reified VM : ViewModel> Fragment.viewModelsByAndroid(
    noinline ownerProducer: () -> ViewModelStoreOwner = { this }
) = viewModels<VM>(ownerProducer) { androidViewModelFactory }

@MainThread
inline fun <reified VM : ViewModel, reified F : Fragment> Fragment.parentViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = viewModels<VM>({
    var parent: Fragment = requireParentFragment()
    while (parent !is F) {
        parent = parent.requireParentFragment()
    }
    parent
}, factoryProducer)

@MainThread
inline fun <reified VM : ViewModel, reified F : Fragment> Fragment.parentViewModelsByAndroid() =
    parentViewModels<VM, F> { androidViewModelFactory }

@MainThread
inline fun <reified VM : ViewModel> Fragment.activityViewModelsByAndroid() =
    activityViewModels<VM> { androidViewModelFactory }