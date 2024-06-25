package com.github.klee0kai.thekey.core.di.debug

import com.github.klee0kai.thekey.core.di.modules.CoreAndroidHelpersModule
import com.github.klee0kai.thekey.core.helpers.path.DummyUserShortPaths

open class DummyCoreAndroidHelpersModule(
    val origin: CoreAndroidHelpersModule
) : CoreAndroidHelpersModule by origin {

    override fun provideUserShortPaths(): DummyUserShortPaths = DummyUserShortPaths()

}
