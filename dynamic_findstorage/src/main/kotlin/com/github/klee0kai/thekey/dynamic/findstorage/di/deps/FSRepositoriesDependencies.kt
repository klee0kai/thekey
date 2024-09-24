package com.github.klee0kai.thekey.dynamic.findstorage.di.deps

import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.dynamic.findstorage.data.FSSettingsRepository
import com.github.klee0kai.thekey.dynamic.findstorage.data.filesystem.FileSystemRepository

interface FSRepositoriesDependencies {

    fun fsSettingsRepositoryLazy(): AsyncCoroutineProvide<FSSettingsRepository>

    fun fsFileSystemRepositoryLazy(): AsyncCoroutineProvide<FileSystemRepository>

}