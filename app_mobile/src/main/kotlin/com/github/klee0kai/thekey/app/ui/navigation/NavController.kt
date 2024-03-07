package com.github.klee0kai.thekey.app.ui.navigation

import android.content.Intent
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KClass

interface AppRouter {

    fun navigate(destination: Destination): Flow<Any?>

    fun <R : Any> navigate(destination: Destination, clazz: KClass<R>): Flow<R?>

    fun <R : Any> backWithResult(result: R, exitFromApp: Boolean = false): Boolean

    suspend fun awaitScreenEvent(destination: Destination)

    fun back()

    fun navigate(intent: Intent): Flow<Intent>

    fun askPermissions(perms: Array<String>): Flow<Boolean>

}


inline fun <reified R : Any> AppRouter.navigate(destination: Destination): Flow<R?> =
    navigate(destination, R::class)
