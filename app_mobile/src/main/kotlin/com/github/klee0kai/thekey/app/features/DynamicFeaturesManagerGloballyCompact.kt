package com.github.klee0kai.thekey.app.features

//import com.github.klee0kai.thekey.app.di.DI
//import com.github.klee0kai.thekey.app.features.model.DynamicFeature
//import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallManagerFactory
//import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallRequest
//import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallSessionStatus
//import com.jeppeman.globallydynamic.globalsplitinstall.GlobalSplitInstallUpdatedListener
//import kotlinx.coroutines.flow.MutableStateFlow
//import timber.log.Timber
//
//class DynamicFeaturesManagerGloballyCompact : DynamicFeaturesManager {
//
//    private val manager = GlobalSplitInstallManagerFactory.create(DI.ctx());
//    override val installedFeatures = MutableStateFlow<List<DynamicFeature>>(emptyList())
//
//    init {
//        val installListener = GlobalSplitInstallUpdatedListener { state ->
//            Timber.d("install status ${state.sessionId()} is ${state.status()} modules ${state.moduleNames()} intent ${state.resolutionIntent()}")
//            when (state.status()) {
//                GlobalSplitInstallSessionStatus.PENDING -> {}
//
//                GlobalSplitInstallSessionStatus.REQUIRES_USER_CONFIRMATION -> {
//
//                }
//
//                GlobalSplitInstallSessionStatus.DOWNLOADING -> {}
//                GlobalSplitInstallSessionStatus.DOWNLOADED -> {}
//                GlobalSplitInstallSessionStatus.INSTALLING -> {
//
//                }
//
//                GlobalSplitInstallSessionStatus.INSTALLED -> {
//                    updateInstalledFeatures()
//                }
//
//                GlobalSplitInstallSessionStatus.FAILED -> {}
//                GlobalSplitInstallSessionStatus.CANCELING -> {}
//                GlobalSplitInstallSessionStatus.CANCELED -> {}
//                else -> {
//                    Timber.e("unknown global split install state ${state.status()}")
//                }
//            }
//        }
//        manager.registerListener(installListener)
//        updateInstalledFeatures()
//    }
//
//    override fun install(feature: DynamicFeature) {
//        Timber.d("install ${feature.moduleName}")
//
//        manager.startInstall(
//            GlobalSplitInstallRequest
//                .newBuilder()
//                .addModule(feature.moduleName)
//                .build()
//        ).addOnSuccessListener { sessionId ->
//            Timber.d("success install ${feature.moduleName} in $sessionId")
//        }.addOnFailureListener { exception ->
//            Timber.d("error install ${feature.moduleName} error $exception")
//            Timber.w(IllegalStateException("Error on feature install", exception))
//        }
//    }
//
//    private fun updateInstalledFeatures() {
//        val installed = manager.installedModules
//        installedFeatures.value = DynamicFeature.allFeatures()
//            .filter { it.moduleName in installed }
//    }
//
//}