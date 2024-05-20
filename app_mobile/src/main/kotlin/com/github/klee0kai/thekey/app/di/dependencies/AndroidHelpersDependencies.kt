package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.core.feature.DynamicFeaturesManager
import com.github.klee0kai.thekey.app.perm.PermissionsHelper
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver

interface AndroidHelpersDependencies {

    fun permissionsHelperLazy(): AsyncCoroutineProvide<PermissionsHelper>

    fun screenResolver(): ScreenResolver

    fun permissionsHelper(): PermissionsHelper

    fun router(): AppRouter

    fun dynamicFeaturesManager(): AsyncCoroutineProvide<DynamicFeaturesManager>

}