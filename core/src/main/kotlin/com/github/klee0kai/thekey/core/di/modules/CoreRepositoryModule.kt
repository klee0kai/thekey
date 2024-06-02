package com.github.klee0kai.thekey.core.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.core.data.repository.settings.SettingsRepository

@Module
interface CoreRepositoryModule {

    @Provide(cache = Provide.CacheType.Soft)
    fun settingsRepository(): SettingsRepository

}