package per.goweii.android.anole

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import per.goweii.android.anole.databinding.ActivityScanBinding
import per.goweii.codex.processor.zxing.ZXingScanProcessor

@SuppressLint("MissingPermission")
class ScanActivity : AppCompatActivity() {
    companion object {
        private const val REQ_CODE_CAMERA = 1001
    }

    private lateinit var binding: ActivityScanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivClose.setOnClickListener {
            finish()
        }
        binding.codeScanner.apply {
            addProcessor(ZXingScanProcessor())
            addDecorator(binding.finderView)
            onFound {
                startActivity(Intent(this@ScanActivity, MainActivity::class.java).apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(it.first().text)
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                })
                finish()
            }
            bindToLifecycle(this@ScanActivity)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
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
                    finish()
                }
            }
        }
    }
}