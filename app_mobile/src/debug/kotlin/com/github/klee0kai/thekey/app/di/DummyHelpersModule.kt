package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.app.di.modules.HelpersModule
import com.github.klee0kai.thekey.app.helpers.DummyUserShortPaths
import com.github.klee0kai.thekey.app.helpers.path.PathInputHelper

@Module
open class DummyHelpersModule : HelpersModule() {

    override fun providePathInputHelper(): PathInputHelper = PathInputHelper()

    override fun provideUserShortPaths(): DummyUserShortPaths = DummyUserShortPaths()

}