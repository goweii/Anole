package per.goweii.anole.ability.impl

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

class ResultApi {
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
                f.startActivityForResult(intent, requestCode)
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
                f.startActivityForResult(intent, requestCode)
            }
        }

        fun getPermissionsResult(
            activity: Activity,
            onRequestPermissionsResult: (
                requestCode: Int,
                permissions: Array<out String>,
                grantResults: IntArray
            ) -> Unit
        ) {
            if (activity is AppCompatActivity) {
                val tag = ResultFragmentX::class.java.name
                val fm = activity.supportFragmentManager
                val f = fm.findFragmentByTag(tag) as? ResultFragmentX?
                    ?: ResultFragmentX().also {
                        fm.beginTransaction().add(it, tag).commit()
                    }
                f.onRequestPermissionsResult = onRequestPermissionsResult
            } else {
                val tag = ResultFragment::class.java.name
                val fm = activity.fragmentManager
                val f = fm.findFragmentByTag(tag) as? ResultFragment?
                    ?: ResultFragment().also {
                        fm.beginTransaction().add(it, tag).commit()
                    }
                f.onRequestPermissionsResult = onRequestPermissionsResult
            }
        }
    }
}

class ResultFragmentX : Fragment() {
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