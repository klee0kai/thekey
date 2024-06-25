package com.github.klee0kai.thekey.core.di.dependecies

import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.core.domain.model.feature.DynamicFeaturesManager
import com.github.klee0kai.thekey.core.helpers.path.PathInputHelper
import com.github.klee0kai.thekey.core.helpers.path.UserShortPaths
import com.github.klee0kai.thekey.core.ui.devkit.theme.AppThemeManager
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver

interface CoreAndroidHelpersDependencies {

    fun themeManager(activity: ActivityIdentifier? = null): AppThemeManager

    fun screenResolver(): ScreenResolver

    fun router(activity: ActivityIdentifier? = null): AppRouter

    fun dynamicFeaturesManager(): AsyncCoroutineProvide<DynamicFeaturesManager>

    fun userShortPaths(): UserShortPaths

    fun pathInputHelper(): PathInputHelper

}