package com.github.klee0kai.thekey.app.ui

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
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.navigation.MainNavContainer
import com.github.klee0kai.thekey.app.ui.navigation.contracts.SimpleActivityContract
import timber.log.Timber

class MainActivity : ComponentActivity() {

    private var reqPermLauncher: ActivityResultLauncher<Array<String>>? = null
    private var reqRawFileManager: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reqPermLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }
        reqRawFileManager = registerForActivityResult(SimpleActivityContract()) { }

        setContent {
            AppTheme {
                MainNavContainer()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        DI.activity(this)
    }

}