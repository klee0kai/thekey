package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.data.SettingsRepository
import com.github.klee0kai.thekey.app.data.StorageFilesRepository

@Module
interface RepositoriesModule {

    @Provide(cache = Provide.CacheType.Soft)
    fun storagesRepository(): StorageFilesRepository

    @Provide(cache = Provide.CacheType.Soft)
    fun settingsRepository(): SettingsRepository

}