package com.github.klee0kai.thekey.app.features

import android.content.pm.PackageInstaller
import android.util.SparseArray
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import com.github.klee0kai.thekey.app.features.model.InstallDynamicFeature
import com.github.klee0kai.thekey.app.features.model.InstallStatus
import com.github.klee0kai.thekey.app.features.model.Installed
import com.github.klee0kai.thekey.app.features.model.Installing
import com.github.klee0kai.thekey.app.features.model.NotInstalled
import com.github.klee0kai.thekey.core.utils.common.launchSafe
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber

class InstallTracker {

    val features = MutableStateFlow<List<InstallDynamicFeature>>(emptyList())

    private val scope = DI.defaultThreadScope()
    private val splitInstallManager = SplitInstallManagerFactory.create(DI.ctx());
    private val packageInstaller: PackageInstaller = DI.ctx().packageManager.packageInstaller
    private val activeSessions = SparseArray<String>()

    init {
        updateOnStart()
        scope.launch(DI.mainDispatcher()) {
            packageInstaller.registerSessionCallback(object : PackageInstaller.SessionCallback() {
                override fun onCreated(sessionId: Int) {
                    scope.launchSafe {
                        Timber.d("install on created $sessionId")
                    }
                }

                override fun onBadgingChanged(sessionId: Int) {
                    scope.launchSafe {
                        Timber.d("install onBadgingChanged $sessionId")
                    }
                }

                override fun onActiveChanged(sessionId: Int, active: Boolean) {
                    scope.launchSafe {
                        Timber.d("install onActiveChanged $sessionId active $active")
                    }
                }

                override fun onProgressChanged(sessionId: Int, progress: Float) {
                    scope.launchSafe {
                        Timber.d("install onProgressChanged $sessionId progress $progress")
                        val moduleName = activeSessions.get(sessionId, null) ?: return@launchSafe
                        val feature = DynamicFeature.byName(moduleName) ?: return@launchSafe
                        update(feature = feature, status = Installing(progress = progress))
                    }
                }

                override fun onFinished(sessionId: Int, success: Boolean) {
                    scope.launchSafe {
                        Timber.d("install onFinished $sessionId success ${success}")
                        val moduleName = activeSessions.get(sessionId, null) ?: return@launchSafe
                        val feature = DynamicFeature.byName(moduleName) ?: return@launchSafe
                        update(feature = feature, status = if (success) Installed else NotInstalled)

                    }
                }
            })
        }
    }

    fun startInstall(sessionId: Int, feature: DynamicFeature) = scope.launchSafe {
        activeSessions[sessionId] = feature.moduleName
        update(feature = feature, status = Installing(progress = 0f))
    }

    fun updateOnStart() = scope.launchSafe {
        val installed = splitInstallManager.installedModules
        features.value = DynamicFeature.allFeatures()
            .map { feature ->
                InstallDynamicFeature(
                    feature = feature,
                    status = when {
                        feature.moduleName in installed -> Installed
                        else -> NotInstalled
                    }
                )
            }
    }

    fun abortAllInstallations() = scope.launchSafe {
        packageInstaller
            .mySessions
            .forEach { packageSession ->
                runCatching {
                    Timber.d("try abandonSession ${packageSession.sessionId}")
                    packageInstaller.abandonSession(packageSession.sessionId)
                }
            }
    }

    private fun update(feature: DynamicFeature, status: InstallStatus) {
        features.update { featuresList ->
            featuresList.filter { it.feature.moduleName != feature.moduleName }
                .toMutableList()
                .apply {
                    add(
                        InstallDynamicFeature(
                            feature = feature,
                            status = status
                        )
                    )
                }
        }
    }

}