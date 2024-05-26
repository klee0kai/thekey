package com.github.klee0kai.thekey.core.di

import com.github.klee0kai.stone.annotations.component.Init
import com.github.klee0kai.stone.annotations.component.ModuleOriginFactory
import com.github.klee0kai.thekey.core.di.modules.CoreAndroidHelpersModule
import com.github.klee0kai.thekey.core.di.modules.CoreInteractorsModule
import com.github.klee0kai.thekey.core.di.modules.CoroutineModule
import com.github.klee0kai.thekey.core.di.modules.ThemeModule

interface CoreComponentModules {

    /* get module */
    fun theme(): ThemeModule
    fun coreAndroidHelpersModule(): CoreAndroidHelpersModule
    fun coreInteractorsModule(): CoreInteractorsModule
    fun coroutinesModule(): CoroutineModule

    /* get origin factories */
    @ModuleOriginFactory
    fun themeFactory(): ThemeModule

    @ModuleOriginFactory
    fun coreAndroidHelpersModuleFactory(): CoreAndroidHelpersModule

    @ModuleOriginFactory
    fun coreInteractorsFactory(): CoreInteractorsModule

    @ModuleOriginFactory
    fun coroutinesModuleFactory(): CoroutineModule

    /* set origin factories */
    @Init
    fun initThemeModule(themeModule: ThemeModule)

    @Init
    fun initCoreAndroidHelpersModule(themeModule: CoreAndroidHelpersModule)

    @Init
    fun initCoreInteractorsModule(interactorsModule: CoreInteractorsModule)

    @Init
    fun initCoroutineModule(coroutineModule: CoroutineModule)

}