package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.app.perm.PermissionsHelper
import com.github.klee0kai.thekey.app.ui.navigation.AppRouter

interface AndroidHelpersDependencies {

    fun permissions(): AsyncCoroutineProvide<PermissionsHelper>

    fun router(): AppRouter

}