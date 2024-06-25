package com.github.klee0kai.thekey.dynamic.findstorage.di

import com.github.klee0kai.stone.annotations.component.Init
import com.github.klee0kai.thekey.app.di.dependencies.AppComponentProviders
import com.github.klee0kai.thekey.core.di.dependecies.CoreDependencyProvider
import com.github.klee0kai.thekey.dynamic.findstorage.di.modules.FSInteractorsModule
import com.github.klee0kai.thekey.dynamic.findstorage.di.modules.FSPresentersModule
import com.github.klee0kai.thekey.dynamic.findstorage.di.modules.FSRepositoriesModule

interface FSModules {

    fun coreDeps(): CoreDependencyProvider

    fun appDeps(): AppComponentProviders

    fun repositoriesModule(): FSRepositoriesModule

    fun interactorsModule(): FSInteractorsModule

    fun presentersModule(): FSPresentersModule

    @Init
    fun initAppDeps(deps: AppComponentProviders)

    @Init
    fun initCoreDeps(deps: CoreDependencyProvider)

    @Init
    fun initRepositoriesModule(deps: FSRepositoriesModule)

    @Init
    fun initFSInteractorsModule(deps: FSInteractorsModule)

    @Init
    fun initFSPresentersModule(deps: FSPresentersModule)

}