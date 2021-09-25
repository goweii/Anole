package per.goweii.android.anole.utils

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

@MainThread
inline fun <reified F : Fragment, reified VM : ViewModel> Fragment.parentViewModels(
    noinline factoryProducer: (() -> ViewModelProvider.Factory)? = null
) = viewModels<VM>({
    var parent: Fragment = requireParentFragment()
    while (parent !is F) {
        parent = parent.requireParentFragment()
    }
    parent
}, factoryProducer)