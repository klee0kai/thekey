package com.github.klee0kai.thekey.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.navigation.MainNavContainer
import com.github.klee0kai.thekey.app.ui.navigation.contracts.SimpleActivityContract
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private val writeStoragesPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private var reqPermLauncher: ActivityResultLauncher<Array<String>>? = null
    private var reqRawFileManager: ActivityResultLauncher<Intent>? = null
    private var permRegDone = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reqPermLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }
        reqRawFileManager = registerForActivityResult(SimpleActivityContract()) { }

        setContent {
            AppTheme {
                MainNavContainer()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissions()
    }

    private fun checkPermissions() {
        writeStoragesPermissions.forEach { perm ->
            val notGranted =
                ActivityCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
            if (notGranted) {
                val showRationale = shouldShowRequestPermissionRationale(perm)
                if (permRegDone && !showRationale) {
                    // user also CHECKED "never ask again"
                    Toast.makeText(this, R.string.approve_permissions, Toast.LENGTH_SHORT).show()
                } else reqPermLauncher?.launch(writeStoragesPermissions)
                return
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            try {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.data = Uri.parse("package:${packageName}")
                reqRawFileManager?.launch(intent)
            } catch (e: Exception) {
                Timber.e(e)
            }
            return
        }
    }
}