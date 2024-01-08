package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.app.helpers.path.PathInputHelper
import com.github.klee0kai.thekey.app.helpers.path.UserShortPaths

@Module
abstract class HelpersModule {

    abstract fun providePathInputHelper(): PathInputHelper

    abstract fun provideUserShortPaths(): UserShortPaths

}