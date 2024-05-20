package com.github.klee0kai.thekey.app.ui.navigation.impl

import android.content.Intent
import com.github.klee0kai.thekey.app.ui.navigation.ActivityRouter
import com.github.klee0kai.thekey.app.ui.navigation.RouterContext
import com.github.klee0kai.thekey.app.ui.navigation.model.ActivityResult
import com.github.klee0kai.thekey.core.utils.coroutine.shareLatest
import com.github.klee0kai.thekey.core.utils.coroutine.singleEventFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ActivityRouterImpl(
    context: RouterContext
) : ActivityRouter, RouterContext by context {

    private val results = MutableSharedFlow<ActivityResult>(replay = 3)

    override fun navigate(intent: Intent): Flow<ActivityResult> = singleEventFlow {
        val reqCode = genRequestCode()
        activity?.startActivityForResult(intent, reqCode)

        results.first { it.requestCode == reqCode }
    }.shareLatest(scope)

    override fun onResult(result: ActivityResult) {
        scope.launch { results.emit(result) }
    }


}