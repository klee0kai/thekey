package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.helpers.path.PathInputHelper
import com.github.klee0kai.thekey.app.helpers.path.UserShortPaths

interface HelperDependencies {

    fun pathInputHelper(): PathInputHelper

    fun userShortPaths(): UserShortPaths


}