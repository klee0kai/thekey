package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.utils.path.PathInputHelper
import com.github.klee0kai.thekey.app.utils.path.UserShortPaths

interface HelperDependencies {

    fun pathInputHelper(): PathInputHelper

    fun userShortPaths(): UserShortPaths


}