package com.github.klee0kai.thekey.core.di.debug

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.core.di.modules.CoreAndroidHelpersModule
import com.github.klee0kai.thekey.core.helpers.path.DummyUserShortPaths

@Module
open class DummyCoreAndroidHelpersModule : CoreAndroidHelpersModule {

    override fun provideUserShortPaths(): DummyUserShortPaths = DummyUserShortPaths()

}