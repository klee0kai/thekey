package com.github.klee0kai.thekey.app.perm

import android.os.Build
import com.github.klee0kai.thekey.app.perm.impl.ManageStoragePermUnit
import com.github.klee0kai.thekey.app.perm.impl.SimplePermUnit
import com.github.klee0kai.thekey.app.perm.impl.SimplePermUnit.Companion.WriteExternalStorage
import com.github.klee0kai.thekey.core.perm.PermUnit
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider
import kotlinx.coroutines.flow.last

open class PermissionsHelper {

    /**
     * we get a list of required permissions to read and use storage directly
     */
    open fun writeStoragePermissions() = buildList {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            add(ManageStoragePermUnit())
        } else {
            add(SimplePermUnit(listOf(WriteExternalStorage)))
        }

    }

    /**
     * check all permissions is granted
     */
    open fun checkPermissions(perms: List<PermUnit>) = perms.all { it.isGranted() }


    /**
     * @return true is success
     */
    open suspend fun askPermissionsIfNeed(perms: List<PermUnit>, purpose: TextProvider): Boolean = perms.all { it.ask(purpose).last() }

}