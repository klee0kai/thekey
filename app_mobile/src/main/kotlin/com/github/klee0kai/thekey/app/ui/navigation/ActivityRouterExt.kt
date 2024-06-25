package com.github.klee0kai.thekey.app.ui.navigation

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.ui.navigation.ActivityRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.ActivityResult
import kotlinx.coroutines.flow.Flow


fun ActivityRouter.navigateAppSettings(): Flow<ActivityResult> {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", DI.ctx().packageName, null)
    intent.setData(uri)
    return navigate(intent)
}
