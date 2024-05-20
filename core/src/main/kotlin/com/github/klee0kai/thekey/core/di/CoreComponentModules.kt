package com.github.klee0kai.thekey.core.di

import com.github.klee0kai.stone.annotations.component.Init
import com.github.klee0kai.stone.annotations.component.ModuleOriginFactory
import com.github.klee0kai.thekey.core.di.modules.CoreInteractorsModule
import com.github.klee0kai.thekey.core.di.modules.ThemeModule

interface CoreComponentModules {

    /* get module */

    fun coreInteractors(): CoreInteractorsModule
    fun theme(): ThemeModule

    /* get origin factories */
    @ModuleOriginFactory
    fun coreInteractorsFactory(): CoreInteractorsModule

    @ModuleOriginFactory
    fun themeFactory(): ThemeModule


    /* set origin factories */
    @Init
    fun initCoreInteractorsModule(interactorsModule: CoreInteractorsModule)

    @Init
    fun initThemeModule(themeModule: ThemeModule)

}