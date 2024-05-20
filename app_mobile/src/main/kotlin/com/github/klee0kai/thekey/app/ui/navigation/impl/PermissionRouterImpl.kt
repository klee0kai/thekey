package com.github.klee0kai.thekey.app.ui.navigation.impl

import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.PermissionsRouter
import com.github.klee0kai.thekey.app.ui.navigation.RouterContext
import com.github.klee0kai.thekey.core.ui.navigation.model.RequestPermResult
import com.github.klee0kai.thekey.core.utils.coroutine.shareLatest
import com.github.klee0kai.thekey.core.utils.coroutine.singleEventFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PermissionRouterImpl(context: RouterContext) : PermissionsRouter, RouterContext by context {

    private val results = MutableSharedFlow<RequestPermResult>(replay = 3)

    override fun askPermissions(perms: Array<String>): Flow<Boolean> = singleEventFlow {
        val reqCode = genRequestCode()

        activity?.requestPermissions(perms, reqCode)

        results.first { it.requestCode == reqCode }

        val allGranted = perms.all { permission ->
            ContextCompat.checkSelfPermission(
                DI.ctx(),
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        allGranted
    }.shareLatest(scope)


    override fun onResult(result: RequestPermResult) {
        scope.launch { results.emit(result) }
    }
}