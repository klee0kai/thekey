package com.github.klee0kai.thekey.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.configRouting
import com.github.klee0kai.thekey.app.ui.navigation.MainNavContainer
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginApplyingOverlay
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.theme.modifyTransparent

class MainTranparentActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DI.configRouting(activityIdentifier)
        DI.themeManager(activityIdentifier)
            .modifyTransparent(transparent = true)
        val processed = DI.router(activityIdentifier)
            .handleDeeplink(intent)

        if (!processed) finish()


        setContent {
            AppTheme(activityIdentifier = activityIdentifier) {
                PluginApplyingOverlay {
                    MainNavContainer()
                }
            }
        }

    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        DI.router().handleDeeplink(intent)
    }

}