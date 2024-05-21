package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.perm.PermissionsHelper
import com.github.klee0kai.thekey.core.di.wrap.AsyncCoroutineProvide

interface AndroidHelpersDependencies {

    fun permissionsHelperLazy(): AsyncCoroutineProvide<PermissionsHelper>

    fun permissionsHelper(): PermissionsHelper

}