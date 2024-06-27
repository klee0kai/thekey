package com.github.klee0kai.thekey.app.ui.navigation.impl

import android.content.Intent
import android.content.IntentSender
import com.github.klee0kai.thekey.core.ui.navigation.ActivityRouter
import com.github.klee0kai.thekey.core.ui.navigation.RouterContext
import com.github.klee0kai.thekey.core.ui.navigation.model.ActivityResult
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
        val result = runCatching {
            activity?.startActivityForResult(intent, reqCode)
        }
        if (result.isSuccess) {
            results.first { it.requestCode == reqCode }
        } else {
            ActivityResult(reqCode, error = result.exceptionOrNull())
        }
    }.shareLatest(scope)

    override fun navigate(sender: IntentSender) = singleEventFlow {
        val reqCode = genRequestCode()
        val result = runCatching {
            activity?.startIntentSenderForResult(
                intent = sender,
                requestCode = reqCode,
                fillInIntent = null,
                flagsMask = 0,
                flagsValues = 0,
                extraFlags = 0,
                options = null,
            )
        }
        if (result.isSuccess) {
            results.first { it.requestCode == reqCode }
        } else {
            ActivityResult(reqCode, error = result.exceptionOrNull())
        }
    }.shareLatest(scope)

    override fun onResult(result: ActivityResult) {
        scope.launch { results.emit(result) }
    }


}