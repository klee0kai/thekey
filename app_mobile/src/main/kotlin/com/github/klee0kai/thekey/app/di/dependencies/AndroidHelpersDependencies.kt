package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.perm.PermissionsHelper

interface AndroidHelpersDependencies {

    fun permissionsHelper(): PermissionsHelper

}