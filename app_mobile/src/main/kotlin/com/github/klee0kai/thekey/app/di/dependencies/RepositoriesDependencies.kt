package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.stone.wrappers.AsyncProvide
import com.github.klee0kai.thekey.app.data.FoundStoragesRepository
import com.github.klee0kai.thekey.app.data.SettingsRepository

interface RepositoriesDependencies {

    fun foundStoragesRepositoryLazy(): AsyncProvide<FoundStoragesRepository>

    fun settingsRepositoryLazy(): AsyncProvide<SettingsRepository>

}