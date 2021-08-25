package per.goweii.anole.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class ResultUtils {
    companion object {
        fun startActivityResult(
            activity: Activity,
            intent: Intent,
            requestCode: Int,
            onActivityResult: (resultCode: Int, data: Intent?) -> Unit
        ) {
            if (activity is AppCompatActivity) {
                val tag = ResultFragmentX::class.java.name
                val fm = activity.supportFragmentManager
                val f = fm.findFragmentByTag(tag) as? ResultFragmentX?
                    ?: ResultFragmentX().also {
                        fm.beginTransaction().add(it, tag).commit()
                    }
                f.onActivityResult = { reqCode, resultCode, data ->
                    if (reqCode == requestCode) {
                        onActivityResult.invoke(resultCode, data)
                    }
                }
                f.doOnAttached {
                    startActivityForResult(intent, requestCode)
                }
            } else {
                val tag = ResultFragment::class.java.name
                val fm = activity.fragmentManager
                val f = fm.findFragmentByTag(tag) as? ResultFragment?
                    ?: ResultFragment().also {
                        fm.beginTransaction().add(it, tag).commit()
                    }
                f.onActivityResult = { reqCode, resultCode, data ->
                    if (reqCode == requestCode) {
                        onActivityResult.invoke(resultCode, data)
                    }
                }
                f.doOnAttached {
                    startActivityForResult(intent, requestCode)
                }
            }
        }

        fun requestPermissionsResult(
            activity: Activity,
            permissions: Array<out String>,
            requestCode: Int,
            onRequestPermissionsResult: (
                permissions: Array<out String>,
                grantResults: IntArray
            ) -> Unit
        ) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                onRequestPermissionsResult.invoke(permissions, permissions.map {
                    PackageManager.PERMISSION_GRANTED
                }.toIntArray())
                return
            }
            if (activity is AppCompatActivity) {
                val tag = ResultFragmentX::class.java.name
                val fm = activity.supportFragmentManager
                val f = fm.findFragmentByTag(tag) as? ResultFragmentX?
                    ?: ResultFragmentX().also {
                        fm.beginTransaction().add(it, tag).commit()
                    }
                f.onRequestPermissionsResult = { reqCode, permissionResults, grantResults ->
                    if (reqCode == requestCode) {
                        onRequestPermissionsResult.invoke(permissionResults, grantResults)
                    }
                }
                f.doOnAttached {
                    requestPermissions(permissions, requestCode)
                }
            } else {
                val tag = ResultFragment::class.java.name
                val fm = activity.fragmentManager
                val f = fm.findFragmentByTag(tag) as? ResultFragment?
                    ?: ResultFragment().also {
                        fm.beginTransaction().add(it, tag).commit()
                    }
                f.onRequestPermissionsResult = { reqCode, permissionResults, grantResults ->
                    if (reqCode == requestCode) {
                        onRequestPermissionsResult.invoke(permissionResults, grantResults)
                    }
                }
                f.doOnAttached {
                    requestPermissions(permissions, requestCode)
                }
            }
        }
    }
}

class ResultFragmentX : Fragment() {
    private var onAttached: (Fragment.() -> Unit)? = null
    internal var onActivityResult: ((
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) -> Unit)? = null
    internal var onRequestPermissionsResult: ((
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) -> Unit)? = null

    fun doOnAttached(onAttached: (Fragment.() -> Unit)) {
        this.onAttached = onAttached
        if (this.isAdded) {
            onAttached.invoke(this)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        onAttached?.invoke(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityResult?.invoke(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult?.invoke(requestCode, permissions, grantResults)
    }
}

class ResultFragment : android.app.Fragment() {
    private var onAttached: (android.app.Fragment.() -> Unit)? = null
    internal var onActivityResult: ((
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) -> Unit)? = null
    internal var onRequestPermissionsResult: ((
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) -> Unit)? = null

    fun doOnAttached(onAttached: (android.app.Fragment.() -> Unit)) {
        this.onAttached = onAttached
        if (this.isAdded) {
            onAttached.invoke(this)
        }
    }

    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
        onAttached?.invoke(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        onActivityResult?.invoke(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult?.invoke(requestCode, permissions, grantResults)
    }
}