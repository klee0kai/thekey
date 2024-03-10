package com.github.klee0kai.thekey.app.perm.impl

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.perm.PermUnit
import com.github.klee0kai.thekey.app.ui.navigation.model.AlertDialogDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.ConfirmDialogResult
import com.github.klee0kai.thekey.app.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.app.ui.navigation.navigate
import com.github.klee0kai.thekey.app.ui.navigation.navigateManageExternalStorage
import com.github.klee0kai.thekey.app.utils.common.singleEventFlow
import com.github.klee0kai.thekey.app.utils.coroutine.shareLatest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last

@RequiresApi(Build.VERSION_CODES.R)
class ManageStoragePermUnit : PermUnit {

    val scope by lazy { DI.mainThreadScope() }
    val router by lazy { DI.router() }

    val app get() = DI.app()

    override fun isGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(app, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    override fun ask(purpose: TextProvider): Flow<Boolean> = singleEventFlow {
        if (isGranted()) return@singleEventFlow true
        val goToSettingsResult = router.navigate<ConfirmDialogResult>(
            AlertDialogDestination(
                title = TextProvider(R.string.grant_permissions),
                message = TextProvider(buildString {
                    appendLine(purpose.text(app.resources))
                    appendLine(app.resources.getString(R.string.neen_permissions_list))
                    appendLine(app.resources.getString(R.string.manageExnternalStorage))
                }),
                confirm = TextProvider(R.string.go_to_settings),
                reject = TextProvider(R.string.reject),
            )
        ).last()

        if (goToSettingsResult != ConfirmDialogResult.CONFIRMED) {
            return@singleEventFlow false
        }

        router.navigateManageExternalStorage()
            .last()

        isGranted()
    }.shareLatest(scope)

}