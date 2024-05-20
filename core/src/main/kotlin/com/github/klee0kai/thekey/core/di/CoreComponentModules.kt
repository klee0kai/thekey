package com.github.klee0kai.thekey.core.di

import com.github.klee0kai.thekey.core.di.modules.CoreInteractorsModule

interface CoreComponentModules {

    fun coreInteractors(): CoreInteractorsModule

}