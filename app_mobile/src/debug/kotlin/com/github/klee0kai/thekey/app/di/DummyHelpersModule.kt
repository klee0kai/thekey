package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.app.di.modules.HelpersModule
import com.github.klee0kai.thekey.app.helpers.DummyUserShortPaths

@Module
abstract class DummyHelpersModule : HelpersModule() {

    override fun provideUserShortPaths(): DummyUserShortPaths = DummyUserShortPaths()

}