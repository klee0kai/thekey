package com.github.klee0kai.thekey.core.di.dependecies

import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.core.feature.DynamicFeaturesManager
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver

interface CoreAndroidHelpersDependencies {

    fun screenResolver(): ScreenResolver

    fun router(): AppRouter

    fun dynamicFeaturesManager(): AsyncCoroutineProvide<DynamicFeaturesManager>

}