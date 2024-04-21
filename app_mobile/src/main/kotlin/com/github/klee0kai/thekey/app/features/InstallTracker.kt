package com.github.klee0kai.thekey.app.features

import android.content.pm.PackageInstaller
import android.util.SparseArray
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import com.github.klee0kai.thekey.app.features.model.InstallDynamicFeature
import com.github.klee0kai.thekey.app.features.model.Installed
import com.github.klee0kai.thekey.app.features.model.NotInstalled
import com.github.klee0kai.thekey.app.utils.common.launchSafe
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import kotlinx.coroutines.flow.MutableStateFlow

class InstallTracker {

    val features = MutableStateFlow<List<InstallDynamicFeature>>(emptyList())

    private val scope = DI.defaultThreadScope()
    private val splitInstallManager = SplitInstallManagerFactory.create(DI.ctx());
    private val packageInstaller: PackageInstaller = DI.ctx().packageManager.packageInstaller
    private val activeSessions = SparseArray<String>()

    init {
        update()
//        scope.launch(DI.mainDispatcher()) {
//            packageInstaller.registerSessionCallback(object : PackageInstaller.SessionCallback() {
//                override fun onCreated(sessionId: Int) {
//                    Timber.d("install on created $sessionId")
//                }
//
//                override fun onBadgingChanged(sessionId: Int) {
//                    Timber.d("install onBadgingChanged $sessionId")
//                }
//
//                override fun onActiveChanged(sessionId: Int, active: Boolean) {
//                    Timber.d("install onActiveChanged $sessionId active $active")
//                }
//
//                override fun onProgressChanged(sessionId: Int, progress: Float) {
//                    Timber.d("install onProgressChanged $sessionId progress $progress")
//                }
//
//                override fun onFinished(sessionId: Int, success: Boolean) {
//                    Timber.d("install onFinished $sessionId success ${success}")
////                    update()
//                }
//            })
//        }
    }

    fun track(module: String, sessionId: Int) = scope.launchSafe {
        activeSessions[sessionId] = module
    }

    fun update() = scope.launchSafe {
//        val myActiveSessions = packageInstaller
//            .mySessions
//            .filter { isActive }
//            .map { packageSession ->
//                val splitInstallSession = runCatching { splitInstallManager.requestSessionState(packageSession.sessionId) }.getOrNull()
//                packageSession to splitInstallSession
//            }
        val installed = splitInstallManager.installedModules

        features.value = DynamicFeature.allFeatures().map { feature ->
//            val session = myActiveSessions.firstOrNull { it.second?.moduleNames()?.contains(feature.moduleName) == true }
            InstallDynamicFeature(
                feature = feature,
                status = when {
                    feature.moduleName in installed -> Installed
//                    session?.first != null -> Installing(
//                        progress = session.first.progress
//                    )

                    else -> NotInstalled
                }
            )
        }
    }


}