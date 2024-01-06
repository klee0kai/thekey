package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.app.utils.path.PathInputHelper
import com.github.klee0kai.thekey.app.utils.path.UserShortPaths

@Module
abstract class HelpersModule {

    abstract fun providePathInputHelper(): PathInputHelper

    abstract fun provideUserShortPaths(): UserShortPaths

}