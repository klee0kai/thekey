package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.core.ui.devkit.color.CommonColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.typography.AppTypeScheme
import com.github.klee0kai.thekey.core.ui.devkit.typography.TypeScheme

@Module
open class ThemeModule {

    open fun colorScheme(): CommonColorScheme = DarkColorScheme()

    open fun typeScheme(): TypeScheme = AppTypeScheme()

}