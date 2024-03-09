package com.github.klee0kai.thekey.app.perm.impl

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.perm.PermUnit
import com.github.klee0kai.thekey.app.utils.common.singleEventFlow
import com.github.klee0kai.thekey.app.utils.coroutine.shareLatest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@RequiresApi(Build.VERSION_CODES.R)
class ManageStoragePermUnit : PermUnit {

    val scope by lazy { DI.mainThreadScope() }
    val router by lazy { DI.router() }

    val app get() = DI.app()

    override fun isGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(app, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun ask(purposeRes: Int): Flow<Boolean> = singleEventFlow {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.data = Uri.parse("package:${DI.app().packageName}")
        router.navigate(intent)
            .first()

        isGranted()
    }.shareLatest(scope)

}