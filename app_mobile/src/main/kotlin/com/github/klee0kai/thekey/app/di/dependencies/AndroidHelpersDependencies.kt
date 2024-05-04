package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.app.features.DynamicFeaturesManager
import com.github.klee0kai.thekey.app.perm.PermissionsHelper
import com.github.klee0kai.thekey.app.ui.navigation.AppRouter
import com.github.klee0kai.thekey.app.ui.navigation.screenresolver.ScreenResolver

interface AndroidHelpersDependencies {

    fun permissionsHelperLazy(): AsyncCoroutineProvide<PermissionsHelper>

    fun screenResolver(): ScreenResolver

    fun permissionsHelper(): PermissionsHelper

    fun router(): AppRouter

    fun dynamicFeaturesManager(): AsyncCoroutineProvide<DynamicFeaturesManager>

}