package com.github.klee0kai.thekey.app.perm

import android.os.Build
import androidx.annotation.StringRes
import com.github.klee0kai.thekey.app.perm.impl.ManageStoragePermUnit
import com.github.klee0kai.thekey.app.perm.impl.SimplePermUnit
import com.github.klee0kai.thekey.app.perm.impl.SimplePermUnit.Companion.WriteExternalStorage
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.last

open class PermissionsHelper {

    /**
     * we get a list of required permissions to read and use storage directly
     */
    open fun writeStoragePermissions() = buildList {
        // higher rights at first.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            add(ManageStoragePermUnit())
        }

        add(SimplePermUnit(listOf(WriteExternalStorage)))

    }.toImmutableList()

    /**
     * check all permissions is granted
     */
    open fun checkPermissions(perms: List<PermUnit>) = perms.all { it.isGranted() }


    /**
     * @return true is success
     */
    open suspend fun askPermissions(perms: List<PermUnit>, @StringRes purpose: Int): Boolean = perms.all { it.ask(purpose).last() }

}