package com.github.klee0kai.thekey.dynamic.findstorage.perm

import android.os.Build
import com.github.klee0kai.thekey.app.perm.PermissionsHelper
import com.github.klee0kai.thekey.app.perm.impl.SimplePermUnit

fun PermissionsHelper.writeStoragePermissions() = buildList {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        add(ManageStoragePermUnit())
    } else {
        add(SimplePermUnit(listOf(SimplePermUnit.WriteExternalStorage)))
    }
}