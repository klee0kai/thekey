package com.github.klee0kai.thekey.app.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.navigation.MainNavContainer
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginDestination

class MainActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                MainNavContainer()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        router.initIfNeed(PluginDestination("qrcodescanner"))
    }

}