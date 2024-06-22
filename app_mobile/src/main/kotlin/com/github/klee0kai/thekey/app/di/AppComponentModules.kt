package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.stone.annotations.component.Init
import com.github.klee0kai.stone.annotations.component.ModuleOriginFactory
import com.github.klee0kai.thekey.app.di.modules.AndroidHelpersModule
import com.github.klee0kai.thekey.app.di.modules.EngineModule
import com.github.klee0kai.thekey.app.di.modules.HelpersModule
import com.github.klee0kai.thekey.app.di.modules.InteractorsModule
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.di.modules.RepositoriesModule

interface AppComponentModules {

    /* get module */
    fun presenters(): PresentersModule
    fun androidHelpers(): AndroidHelpersModule
    fun helpers(): HelpersModule
    fun interactors(): InteractorsModule
    fun repositories(): RepositoriesModule
    fun engine(): EngineModule

    /* get origin factories */
    @ModuleOriginFactory
    fun presentersFactory(): PresentersModule

    @ModuleOriginFactory
    fun androidHelpersFactory(): AndroidHelpersModule

    @ModuleOriginFactory
    fun helpersFactory(): HelpersModule

    @ModuleOriginFactory
    fun interactorsFactory(): InteractorsModule

    @ModuleOriginFactory
    fun repositoriesFactory(): RepositoriesModule

    @ModuleOriginFactory
    fun engineFactory(): EngineModule

    /* override */
    @Init
    fun initEngineModule(engineModule: Class<out EngineModule>)

    @Init
    fun initHelpersModule(helpers: Class<out HelpersModule>)

    @Init
    fun initAndroidHelpersModule(helpers: AndroidHelpersModule)

    @Init
    fun initPresenterModule(presentersModule: PresentersModule)


}