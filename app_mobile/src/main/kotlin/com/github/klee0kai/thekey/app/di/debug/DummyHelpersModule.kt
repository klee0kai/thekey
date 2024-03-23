package com.github.klee0kai.thekey.app.di.debug

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.app.di.modules.HelpersModule
import com.github.klee0kai.thekey.app.helpers.path.DummyUserShortPaths

@Module
abstract class DummyHelpersModule : HelpersModule() {

    override fun provideUserShortPaths(): DummyUserShortPaths = DummyUserShortPaths()

}