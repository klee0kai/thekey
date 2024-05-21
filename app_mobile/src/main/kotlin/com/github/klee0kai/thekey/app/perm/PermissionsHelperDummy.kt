package com.github.klee0kai.thekey.app.perm

import com.github.klee0kai.thekey.core.perm.PermUnit

open class PermissionsHelperDummy(
    private val permGranted: Boolean = false,
) : PermissionsHelper() {

    override fun checkPermissions(perms: List<PermUnit>) = permGranted

}