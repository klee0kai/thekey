package com.github.klee0kai.thekey.app.perm

import com.github.klee0kai.thekey.app.ui.navigation.model.TextProvider
import kotlinx.coroutines.flow.Flow

interface PermUnit {

    /**
     * check perm is granted
     */
    fun isGranted(): Boolean

    /**
     * run routing flow to ask permission
     */
    fun ask(purpose: TextProvider): Flow<Boolean>

    /**
     * merge with other permissions
     */
    fun mergeWith(units: List<PermUnit>): List<PermUnit> = units

}