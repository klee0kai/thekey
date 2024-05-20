package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.perm.PermissionsHelper

@Module
interface AndroidHelpersModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun permissionsHelper(): PermissionsHelper = PermissionsHelper()

}