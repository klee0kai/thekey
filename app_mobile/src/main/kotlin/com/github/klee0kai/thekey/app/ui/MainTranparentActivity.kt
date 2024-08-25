package com.github.klee0kai.thekey.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.configRouting
import com.github.klee0kai.thekey.app.ui.main.BaseActivity
import com.github.klee0kai.thekey.app.ui.navigation.MainNavContainer
import com.github.klee0kai.thekey.app.ui.navigation.model.EmptyDestination
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginApplyingOverlay
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.overlay.OverlayContainer
import com.github.klee0kai.thekey.core.ui.devkit.theme.modifyTransparent
import kotlinx.coroutines.launch

class MainTranparentActivity : BaseActivity() {

    init {
        router.initDestination(EmptyDestination)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scope.launch {
            DI.configRouting(activityIdentifier)
            themeManager.modifyTransparent(transparent = true)
            val processed = router.handleDeeplink(intent)
            if (!processed) finish()

            setContent {
                AppTheme(activityIdentifier = activityIdentifier) {
                    PluginApplyingOverlay {
                        OverlayContainer {
                            MainNavContainer()
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        scope.launch { DI.router().handleDeeplink(intent) }
    }

}