package com.github.klee0kai.thekey.core.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.core.ui.devkit.color.CommonColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.color.DarkColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.typography.AppTypeScheme
import com.github.klee0kai.thekey.core.ui.devkit.typography.TypeScheme

@Module
interface ThemeModule {

    fun colorScheme(): CommonColorScheme = DarkColorScheme()

    fun typeScheme(): TypeScheme = AppTypeScheme()

}