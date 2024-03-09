package com.github.klee0kai.thekey.app.perm

import androidx.annotation.StringRes
import kotlinx.coroutines.flow.Flow

interface PermUnit {

    /**
     * check perm is granted
     */
    fun isGranted(): Boolean

    /**
     * run routing flow to ask permission
     */
    fun ask(@StringRes purposeRes: Int): Flow<Boolean>

    /**
     * merge with other permissions
     */
    fun mergeWith(units: List<PermUnit>): List<PermUnit> = units

}