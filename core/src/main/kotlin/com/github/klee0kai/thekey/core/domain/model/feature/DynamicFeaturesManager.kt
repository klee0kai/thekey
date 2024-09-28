package com.github.klee0kai.thekey.core.domain.model.feature

import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.domain.model.feature.model.InstallDynamicFeature
import com.github.klee0kai.thekey.core.domain.model.feature.model.InstallStatus
import com.github.klee0kai.thekey.core.domain.model.feature.model.NotInstalled
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull

interface DynamicFeaturesManager {

    val features: StateFlow<List<InstallDynamicFeature>> get() = MutableStateFlow(emptyList())

    fun install(feature: DynamicFeature): Job = Job()

    fun uninstall(feature: DynamicFeature): Job = Job()

}

suspend fun DynamicFeaturesManager.status(feature: DynamicFeature): InstallStatus {
    return features.firstOrNull()
        ?.firstOrNull { it.feature.moduleName == feature.moduleName }
        ?.status
        ?: NotInstalled
}