package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.stone.wrappers.AsyncProvide
import com.github.klee0kai.thekey.app.data.SettingsRepository
import com.github.klee0kai.thekey.app.data.StorageFilesRepository

interface RepositoriesDependencies {

    fun storagesRepositoryLazy(): AsyncProvide<StorageFilesRepository>

    fun settingsRepositoryLazy(): AsyncProvide<SettingsRepository>

}