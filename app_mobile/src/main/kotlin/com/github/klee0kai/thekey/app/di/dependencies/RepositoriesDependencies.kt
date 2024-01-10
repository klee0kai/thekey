package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.data.repositories.FoundStoragesRepository
import com.github.klee0kai.thekey.app.data.repositories.SettingsRepository
import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide

interface RepositoriesDependencies {

    fun foundStoragesRepositoryLazy(): AsyncCoroutineProvide<FoundStoragesRepository>

    fun settingsRepositoryLazy(): AsyncCoroutineProvide<SettingsRepository>

}