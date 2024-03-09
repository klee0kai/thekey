package com.github.klee0kai.thekey.app.ui.navigation

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.github.klee0kai.thekey.app.di.DI
import kotlinx.coroutines.flow.Flow


fun ActivityRouter.navigateAppSettings(): Flow<Intent?> {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", DI.app().packageName, null)
    intent.setData(uri)
    return navigate(intent)
}

