package com.github.klee0kai.thekey.core.perm

import com.github.klee0kai.thekey.core.ui.navigation.AppRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider
import kotlinx.coroutines.flow.Flow

interface PermUnit {

    /**
     * check perm is granted
     */
    fun isGranted(): Boolean

    /**
     * run routing flow to ask permission
     */
    fun AppRouter.ask(purpose: TextProvider, skipDialog: Boolean = false): Flow<Boolean>

    /**
     * merge with other permissions
     */
    fun mergeWith(units: List<PermUnit>): List<PermUnit> = units

}