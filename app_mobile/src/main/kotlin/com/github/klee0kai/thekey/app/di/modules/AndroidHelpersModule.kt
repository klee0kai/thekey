package com.github.klee0kai.thekey.app.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.app.perm.PermissionsHelper

@Module
abstract class AndroidHelpersModule {

    @Provide(cache = Provide.CacheType.Weak)
    abstract fun permissionsHelper(): PermissionsHelper

}