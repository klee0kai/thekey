package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.app.helpers.path.PathInputHelper
import com.github.klee0kai.thekey.app.helpers.path.UserShortPaths

@Module
interface HelpersModule {

    fun providePathInputHelper(): PathInputHelper

    fun provideUserShortPaths(): UserShortPaths

}