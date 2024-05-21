package com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.navigation

import android.Manifest
import com.github.klee0kai.thekey.app.perm.PermissionsHelper
import com.github.klee0kai.thekey.app.perm.impl.SimplePermUnit
import com.github.klee0kai.thekey.core.perm.model.SimplePerm
import com.github.klee0kai.thekey.feature.qrcodescanner.R

fun PermissionsHelper.cameraPermissions() = buildList {
    add(SimplePermUnit(listOf(SimplePermUnit.Camera)))
}

val SimplePermUnit.Companion.Camera: SimplePerm
    get() = SimplePerm(
        perm = Manifest.permission.CAMERA,
        desc = R.string.camera_permission,
    )