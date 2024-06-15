package com.github.klee0kai.thekey.app.ui.login.presenter

import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.utils.coroutine.emptyJob
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface LoginPresenter {

    val currentStorageFlow: Flow<ColoredStorage> get() = emptyFlow()

    fun selectStorage(): Job = emptyJob()

    fun login(passw: String): Job = emptyJob()

}