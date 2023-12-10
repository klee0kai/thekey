package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.data.FoundStoragesRepository
import com.github.klee0kai.thekey.app.data.SettingsRepository
import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide

interface RepositoriesDependencies {

    fun foundStoragesRepositoryLazy(): AsyncCoroutineProvide<FoundStoragesRepository>

    fun settingsRepositoryLazy(): AsyncCoroutineProvide<SettingsRepository>

}