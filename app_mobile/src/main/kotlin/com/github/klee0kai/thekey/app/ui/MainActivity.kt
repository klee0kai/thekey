package com.github.klee0kai.thekey.app.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.configRouting
import com.github.klee0kai.thekey.app.ui.navigation.MainNavContainer
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginApplyingOverlay
import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import kotlinx.coroutines.launch

open class MainActivity : BaseActivity() {

    override val activityIdentifier: ActivityIdentifier? get() = null

    init {
        router.initDestination(LoginDestination)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DI.configRouting()
        scope.launch { DI.router().handleDeeplink(intent) }

        setContent {
            AppTheme {
                PluginApplyingOverlay {
                    MainNavContainer()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        scope.launch { DI.router().handleDeeplink(intent) }
    }

}