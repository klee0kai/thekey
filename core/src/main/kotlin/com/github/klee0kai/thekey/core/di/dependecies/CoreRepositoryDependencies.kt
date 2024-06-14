package com.github.klee0kai.thekey.core.di.dependecies

import com.github.klee0kai.thekey.core.data.repository.settings.SettingsRepository
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide

interface CoreRepositoryDependencies {

    fun settingsRepositoryLazy(): AsyncCoroutineProvide<SettingsRepository>

}