package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.domain.FindStoragesInteractor

@Module
interface InteractorsModule {

    @Provide(cache = Provide.CacheType.Soft)
    fun findStoragesInteractor(): FindStoragesInteractor

}