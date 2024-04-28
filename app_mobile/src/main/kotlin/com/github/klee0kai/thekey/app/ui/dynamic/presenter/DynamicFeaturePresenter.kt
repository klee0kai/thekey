package com.github.klee0kai.thekey.app.ui.dynamic.presenter

import com.github.klee0kai.thekey.app.features.model.InstallStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface DynamicFeaturePresenter {

    val status: Flow<InstallStatus> get() = emptyFlow()

    fun install(): Job = Job()

}