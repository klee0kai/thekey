package com.github.klee0kai.thekey.core.feature

import com.github.klee0kai.thekey.core.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.feature.model.InstallDynamicFeature
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface DynamicFeaturesManager {

    val features: StateFlow<List<InstallDynamicFeature>> get() = MutableStateFlow(emptyList())

    fun install(feature: DynamicFeature): Job = Job()

    fun uninstall(feature: DynamicFeature): Job = Job()

}