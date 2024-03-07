package com.github.klee0kai.thekey.app.ui.navigation.impl

import android.content.Intent
import com.github.klee0kai.thekey.app.ui.navigation.ActivityRouter
import com.github.klee0kai.thekey.app.ui.navigation.RouterContext
import kotlinx.coroutines.flow.Flow

class ActivityRouterImpl(
    context: RouterContext
) : ActivityRouter, RouterContext by context {

    override fun navigate(intent: Intent): Flow<Intent> {
        TODO()
    }

}