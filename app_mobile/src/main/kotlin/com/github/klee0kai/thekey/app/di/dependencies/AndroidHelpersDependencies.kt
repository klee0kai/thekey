package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.perm.PermissionsHelper
import com.github.klee0kai.thekey.core.helpers.path.PathInputHelper

interface AndroidHelpersDependencies {

    fun permissionsHelper(): PermissionsHelper

    fun pathInputHelper(): PathInputHelper

}