package com.github.klee0kai.thekey.dynamic.findstorage.di

import com.github.klee0kai.stone.annotations.component.Init
import com.github.klee0kai.thekey.core.di.dependecies.CoreDependencyProvider
import com.github.klee0kai.thekey.dynamic.findstorage.di.modules.FindStoragesInteractorsModule

interface FindStoragesModules {

    fun coreDeps(): CoreDependencyProvider

    fun interactorsModule(): FindStoragesInteractorsModule

    @Init
    fun initCoreDeps(deps: CoreDependencyProvider)

    @Init
    fun initInteractorsModule(deps: FindStoragesInteractorsModule)

}