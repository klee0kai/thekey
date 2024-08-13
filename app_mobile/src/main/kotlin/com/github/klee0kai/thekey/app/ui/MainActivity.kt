package com.github.klee0kai.thekey.app.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.configRouting
import com.github.klee0kai.thekey.app.service.UnfinishedJobsService
import com.github.klee0kai.thekey.app.ui.navigation.MainNavContainer
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginApplyingOverlay
import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.overlay.OverlayContainer
import com.github.klee0kai.thekey.core.utils.common.GlobalJobsCollection
import kotlinx.coroutines.launch

open class MainActivity : BaseActivity() {

    override val activityIdentifier: ActivityIdentifier? get() = null

    protected val lifeCycleInteractor get() = DI.lifecycleInteractor()

    init {
        router.initDestination(LoginDestination())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DI.configRouting()
        scope.launch { DI.router().handleDeeplink(intent) }

        setContent {
            AppTheme {
                PluginApplyingOverlay {
                    OverlayContainer {
                        MainNavContainer()
                    }
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        lifeCycleInteractor.appMinimazed()
        scope.launch {
            if (GlobalJobsCollection.globalJobs.value.isNotEmpty()
                && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
            ) {
                val intent = Intent(application, UnfinishedJobsService::class.java)
                startForegroundService(intent)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        scope.launch { DI.router().handleDeeplink(intent) }
    }

}