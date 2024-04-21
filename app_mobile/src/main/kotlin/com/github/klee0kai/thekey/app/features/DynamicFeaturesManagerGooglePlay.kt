package com.github.klee0kai.thekey.app.features

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import com.github.klee0kai.thekey.app.features.model.InstallDynamicFeature
import com.google.android.play.core.splitinstall.SplitInstallManagerFactory
import com.google.android.play.core.splitinstall.SplitInstallRequest
import com.google.android.play.core.splitinstall.SplitInstallStateUpdatedListener
import com.google.android.play.core.splitinstall.model.SplitInstallSessionStatus
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class DynamicFeaturesManagerGooglePlay : DynamicFeaturesManager {

    override val features = MutableStateFlow<List<InstallDynamicFeature>>(emptyList())

    private val manager = SplitInstallManagerFactory.create(DI.ctx());

    init {
        val installListener = SplitInstallStateUpdatedListener { state ->
            Timber.d("install status ${state.sessionId()} is ${state.status()} modules ${state.moduleNames()} intent ${state.resolutionIntent()}")
            when (state.status()) {
                SplitInstallSessionStatus.PENDING -> {}

                SplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {

                }

                SplitInstallSessionStatus.DOWNLOADING -> {}
                SplitInstallSessionStatus.DOWNLOADED -> {}
                SplitInstallSessionStatus.INSTALLING -> {

                }

                SplitInstallSessionStatus.INSTALLED -> {
                }

                SplitInstallSessionStatus.FAILED -> {}
                SplitInstallSessionStatus.CANCELING -> {}
                SplitInstallSessionStatus.CANCELED -> {}
                else -> {
                    Timber.e("unknown global split install state ${state.status()}")
                }
            }
        }
        manager.registerListener(installListener)
    }

    override fun install(feature: DynamicFeature) {
        Timber.d("install ${feature.moduleName}")

        manager.startInstall(
            SplitInstallRequest
                .newBuilder()
                .addModule(feature.moduleName)
                .build()
        ).addOnSuccessListener { sessionId ->
            Timber.d("success install ${feature.moduleName} in $sessionId")
        }.addOnFailureListener { exception ->
            Timber.d("error install ${feature.moduleName} error $exception")
            Timber.w(IllegalStateException("Error on feature install", exception))
        }
    }


}