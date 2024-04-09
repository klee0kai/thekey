package com.github.klee0kai.thekey.app.ui.login.presenter

import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow

interface LoginPresenter {

    val currentStorageFlow: Flow<ColoredStorage>

    fun selectStorage(): Job = Job()

    fun login(passw: String): Job = Job()

}