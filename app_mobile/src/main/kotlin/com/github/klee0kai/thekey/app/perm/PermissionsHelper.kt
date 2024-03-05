package com.github.klee0kai.thekey.app.perm

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.perm.model.ApproveResult
import kotlinx.collections.immutable.toImmutableList

class PermissionsHelper {

    /**
     * we get a list of required permissions to read and use storage directly
     */
    fun writeStoragePermissions() = buildList {
        add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            add(Manifest.permission.MANAGE_EXTERNAL_STORAGE)
        }
    }.toImmutableList()


    open fun checkPermissions(permList: List<String>): ApproveResult {
        val results = checkPermissionsDetailed(permList)
            .map { it.second }
            .toSet()
        return when {
            results.contains(ApproveResult.MOCKED) -> ApproveResult.APPROVED
            results.contains(ApproveResult.REJECTED_FOREVER) -> ApproveResult.REJECTED_FOREVER
            results.contains(ApproveResult.REJECTED) -> ApproveResult.REJECTED
            results.contains(ApproveResult.APPROVED) -> ApproveResult.APPROVED
            else -> ApproveResult.MOCKED
        }
    }

    open fun checkPermissionsDetailed(permList: List<String>): List<Pair<String, ApproveResult>> {
        return permList.map { perm ->
            if (DI.config().isViewEditMode) {
                perm to ApproveResult.MOCKED
            } else {
                val notGranted = ActivityCompat.checkSelfPermission(DI.app(), perm) != PackageManager.PERMISSION_GRANTED
                val showRationale = notGranted && DI.activity()?.shouldShowRequestPermissionRationale(perm) == true

                val approveResult = when {
                    notGranted && showRationale -> ApproveResult.REJECTED_FOREVER
                    notGranted -> ApproveResult.REJECTED
                    else -> ApproveResult.APPROVED
                }
                perm to approveResult
            }

        }
    }


}