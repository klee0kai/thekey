package com.github.klee0kai.thekey.app.ui.navigation.deeplink

import android.content.Intent
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.MainActivity
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.core.ui.navigation.deeplink.DeeplinkRoute
import com.github.klee0kai.thekey.core.ui.navigation.deeplink.action
import com.github.klee0kai.thekey.core.ui.navigation.deeplink.activity
import com.github.klee0kai.thekey.core.R as CoreR

fun DeeplinkRoute.openStorageDeeplink() {
    activity(MainActivity::class.qualifiedName ?: "") {
        action(Intent.ACTION_VIEW) {
            handle { intent ->
                val url = intent.data ?: return@handle false
                val engine = DI.findStorageEngineLazy()
                val storage = DI.ctx()
                    .contentResolver
                    .openFileDescriptor(url, "r", null)
                    ?.use {
                        engine().storageInfoFromDescriptor(it.fd)
                    }
                if (storage == null) {
                    snack(CoreR.string.storage_file_incorrect)
                } else {
                    resetStack(LoginDestination(url.toString()))
                }
                true
            }
        }
    }
}