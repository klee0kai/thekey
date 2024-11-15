package com.github.klee0kai.thekey.app.perm.impl

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.navigateAppSettings
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.perm.PermUnit
import com.github.klee0kai.thekey.core.perm.model.SimplePerm
import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.ConfirmDialogResult
import com.github.klee0kai.thekey.core.ui.navigation.model.SimpleDialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.core.ui.navigation.navigate
import com.github.klee0kai.thekey.core.utils.coroutine.shareLatest
import com.github.klee0kai.thekey.core.utils.coroutine.singleEventFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last

class SimplePermUnit(
    private val permissions: List<SimplePerm>,
) : PermUnit {

    private val app get() = DI.ctx()

    override fun isGranted(): Boolean = permissions.all { perm ->
        ActivityCompat.checkSelfPermission(app, perm.perm) == PackageManager.PERMISSION_GRANTED
    }

    override fun AppRouter.ask(purpose: TextProvider, skipDialog: Boolean): Flow<Boolean> = singleEventFlow {
        if (isGranted()) return@singleEventFlow true

        // ask all permissions directly
        askPermissions(permissions.map { it.perm }.toTypedArray())
            .last()

        // check need
        val notGranted = permissions.any { perm -> ActivityCompat.checkSelfPermission(app, perm.perm) != PackageManager.PERMISSION_GRANTED }
        val showRationale = permissions.any { perm -> activity?.shouldShowRequestPermissionRationale(perm.perm) == true }

        if (notGranted && !showRationale) {
            if (!skipDialog) {
                val goToSettingsResult = navigate<ConfirmDialogResult>(
                    SimpleDialogDestination(
                        title = TextProvider(R.string.grant_permissions),
                        message = TextProvider(buildString {
                            appendLine(purpose.text(app.resources))
                            appendLine(app.resources.getString(R.string.neen_permissions_list))

                            permissions.forEach {
                                appendLine(app.resources.getString(it.desc))
                            }
                        }),
                        confirm = TextProvider(R.string.go_to_settings),
                        reject = TextProvider(R.string.reject),
                    )
                ).last()
                if (goToSettingsResult != ConfirmDialogResult.CONFIRMED) {
                    return@singleEventFlow false
                }
            }

            navigateAppSettings()
                .last()
        }

        isGranted()
    }.shareLatest(scope)

    override fun mergeWith(units: List<PermUnit>): List<PermUnit> {
        val simpleUnits = units.filterIsInstance<SimplePermUnit>()
        if (simpleUnits.size <= 1) return units

        val oneSimpleUnit = SimplePermUnit(simpleUnits.flatMap { it.permissions })
        val otherUnits = units.filter { it !is SimplePermUnit }
        return otherUnits + listOf(oneSimpleUnit)
    }


    companion object {

        val WriteExternalStorage = SimplePerm(
            perm = Manifest.permission.WRITE_EXTERNAL_STORAGE,
            desc = R.string.writeExternalStorage,
        )

    }
}