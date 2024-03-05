package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.app.perm.PermissionsHelper

interface AndroidHelpersDependencies {

    fun permissions(): AsyncCoroutineProvide<PermissionsHelper>

}