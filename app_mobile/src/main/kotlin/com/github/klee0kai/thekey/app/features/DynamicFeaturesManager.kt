package com.github.klee0kai.thekey.app.features

import com.github.klee0kai.hummus.collections.contains
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

interface DynamicFeaturesManager {

    val installedFeatures: StateFlow<List<DynamicFeature>> get() = MutableStateFlow(emptyList())

    fun install(feature: DynamicFeature) = Unit

    fun isInstalled(feature: DynamicFeature): Flow<Boolean> = installedFeatures
        .map { features -> features.contains { it.moduleName == feature.moduleName } }

}