package com.github.klee0kai.thekey.dynamic.findstorage.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.dynamic.findstorage.domain.FileSystemInteractor
import com.github.klee0kai.thekey.dynamic.findstorage.domain.FindStorageInteractor

@Module
interface FSInteractorsModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun findStoragesInteractor(): FindStorageInteractor

    @Provide(cache = Provide.CacheType.Weak)
    fun fileSystemInteractor(): FileSystemInteractor

}