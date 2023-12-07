package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.thekey.app.ui.designkit.color.CommonColorScheme
import com.github.klee0kai.thekey.app.ui.designkit.color.DarkColorScheme

@Module
open class ThemeModule {

    open fun colorScheme(): CommonColorScheme = DarkColorScheme()

}