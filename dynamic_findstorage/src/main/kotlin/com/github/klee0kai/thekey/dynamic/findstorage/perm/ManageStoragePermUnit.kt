package com.github.klee0kai.thekey.dynamic.findstorage.perm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.perm.PermUnit
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.ConfirmDialogResult
import com.github.klee0kai.thekey.core.ui.navigation.model.SimpleDialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.core.ui.navigation.navigate
import com.github.klee0kai.thekey.core.utils.coroutine.shareLatest
import com.github.klee0kai.thekey.core.utils.coroutine.singleEventFlow
import com.github.klee0kai.thekey.dynamic.findstorage.ui.navigation.navigateManageExternalStorage
import kotlinx.coroutines.flow.last


@RequiresApi(Build.VERSION_CODES.R)
class ManageStoragePermUnit : PermUnit {

    private val app get() = CoreDI.ctx()

    override fun isGranted(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager()
        } else {
            val write = ContextCompat.checkSelfPermission(app, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            val read = ContextCompat.checkSelfPermission(app, Manifest.permission.READ_EXTERNAL_STORAGE)
            return read == PackageManager.PERMISSION_GRANTED && write == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun AppRouter.ask(purpose: TextProvider, skipDialog: Boolean) = singleEventFlow {
        if (isGranted()) return@singleEventFlow true
        if (!skipDialog) {
            val goToSettingsResult = navigate<ConfirmDialogResult>(
                SimpleDialogDestination(
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
        }

        navigateManageExternalStorage()
            .last()

        isGranted()
    }.shareLatest(scope)


}