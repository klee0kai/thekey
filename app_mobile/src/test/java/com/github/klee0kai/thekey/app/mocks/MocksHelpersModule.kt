package com.github.klee0kai.thekey.app.mocks

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.app.di.modules.HelpersModule
import com.github.klee0kai.thekey.app.mocks.helpers.MocksUserShortPaths
import com.github.klee0kai.thekey.app.utils.path.PathInputHelper
import com.github.klee0kai.thekey.app.utils.path.UserShortPaths

@Module
class MocksHelpersModule : HelpersModule() {

    override fun providePathInputHelper(): PathInputHelper = PathInputHelper()

    override fun provideUserShortPaths(): UserShortPaths = MocksUserShortPaths()

}