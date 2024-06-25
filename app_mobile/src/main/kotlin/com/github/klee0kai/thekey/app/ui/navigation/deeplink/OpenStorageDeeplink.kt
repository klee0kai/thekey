package com.github.klee0kai.thekey.app.ui.navigation.deeplink

import android.content.Intent
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.MainActivity
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
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
                val fd = DI.ctx()
                    .contentResolver
                    .openFileDescriptor(url, "rw", null)
                    ?: return@handle false

                val storage = engine().storageInfoFromDescriptor(fd.fd)

                if (storage == null) {
                    snack(CoreR.string.storage_file_incorrect)
                } else {
                    resetStack(
                        LoginDestination(
                            StorageIdentifier(
                                path = url.toString(),
                                version = storage.version,
                                fileDescriptor = fd.detachFd(),
                            )
                        )
                    )
                }
                true
            }
        }
    }
}