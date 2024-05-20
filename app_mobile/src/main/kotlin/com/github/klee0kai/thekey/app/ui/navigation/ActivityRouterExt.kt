package com.github.klee0kai.thekey.app.ui.navigation

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.core.ui.navigation.model.ActivityResult
import kotlinx.coroutines.flow.Flow


fun ActivityRouter.navigateAppSettings(): Flow<ActivityResult> {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", DI.ctx().packageName, null)
    intent.setData(uri)
    return navigate(intent)
}


@RequiresApi(Build.VERSION_CODES.R)
fun ActivityRouter.navigateManageExternalStorage(): Flow<ActivityResult> {
    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.data = Uri.parse("package:${DI.ctx().packageName}")
    return navigate(intent)
}
