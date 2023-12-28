package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.app.engine.CryptStorageEngine
import com.github.klee0kai.thekey.app.engine.EditStorageEngine
import com.github.klee0kai.thekey.app.engine.FindStorageEngine

interface EngineDependencies {

    fun findStorageEngineLazy(): AsyncCoroutineProvide<FindStorageEngine>

    fun editStorageEngineLazy(): AsyncCoroutineProvide<EditStorageEngine>

    fun cryptStorageEngineLazy(id: StorageIdentifier): AsyncCoroutineProvide<CryptStorageEngine>

}