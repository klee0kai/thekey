package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.data.FoundStoragesRepository
import com.github.klee0kai.thekey.app.data.SettingsRepository

@Module
interface RepositoriesModule {

    @Provide(cache = Provide.CacheType.Soft)
    fun foundStoragesRepository(): FoundStoragesRepository

    @Provide(cache = Provide.CacheType.Soft)
    fun settingsRepository(): SettingsRepository

}