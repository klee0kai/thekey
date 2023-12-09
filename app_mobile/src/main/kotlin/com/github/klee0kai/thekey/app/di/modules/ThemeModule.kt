package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.app.ui.designkit.color.CommonColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.color.DarkColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.typography.AppTypeScheme
import com.github.klee0kai.thekey.app.ui.designkit.typography.TypeScheme

@Module
open class ThemeModule {

    open fun colorScheme(): CommonColorScheme = DarkColorScheme()

    open fun typeScheme(): TypeScheme = AppTypeScheme()

}