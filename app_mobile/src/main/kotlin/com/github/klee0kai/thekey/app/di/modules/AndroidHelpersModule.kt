package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.perm.PermissionsHelper
import com.github.klee0kai.thekey.app.ui.navigation.AppRouter
import com.github.klee0kai.thekey.app.ui.navigation.impl.AppRouterImp

@Module
open class AndroidHelpersModule {

    @Provide(cache = Provide.CacheType.Weak)
    open fun permissionsHelper(): PermissionsHelper = PermissionsHelper()

    @Provide(cache = Provide.CacheType.Weak)
    open fun router(): AppRouter = AppRouterImp()

}