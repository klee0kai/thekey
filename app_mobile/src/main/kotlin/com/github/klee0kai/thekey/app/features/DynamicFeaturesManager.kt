package com.github.klee0kai.thekey.app.features

import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import com.github.klee0kai.thekey.app.features.model.InstallDynamicFeature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface DynamicFeaturesManager {

    val features: StateFlow<List<InstallDynamicFeature>> get() = MutableStateFlow(emptyList())

    fun install(feature: DynamicFeature) = Unit

    fun uninstall(feature: DynamicFeature) = Unit

}