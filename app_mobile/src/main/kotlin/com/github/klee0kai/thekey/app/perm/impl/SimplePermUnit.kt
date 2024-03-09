package com.github.klee0kai.thekey.app.perm.impl

import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.perm.PermUnit
import com.github.klee0kai.thekey.app.perm.model.SimplePerm
import com.github.klee0kai.thekey.app.ui.navigation.navigateAppSettings
import com.github.klee0kai.thekey.app.utils.common.singleEventFlow
import com.github.klee0kai.thekey.app.utils.coroutine.shareLatest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.last

class SimplePermUnit(
    private val permissions: List<SimplePerm>,
) : PermUnit {

    val scope by lazy { DI.mainThreadScope() }
    val router by lazy { DI.router() }

    val activity get() = DI.activity()
    val app get() = DI.app()

    override fun isGranted(): Boolean = permissions.all { perm ->
        ActivityCompat.checkSelfPermission(app, perm.perm) == PackageManager.PERMISSION_GRANTED
    }

    override fun ask(purposeRes: Int): Flow<Boolean> = singleEventFlow {
        // ask all permissions directly
        router.askPermissions(permissions.map { it.perm }.toTypedArray())
            .last()

        // check need
        val needToSetting = permissions.any { perm ->
            val notGranted = ActivityCompat.checkSelfPermission(app, perm.perm) != PackageManager.PERMISSION_GRANTED
            val showRationale = notGranted && activity?.shouldShowRequestPermissionRationale(perm.perm) == true
            showRationale
        }

        if (needToSetting) {
            // TODO show dialog

            router.navigateAppSettings()
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