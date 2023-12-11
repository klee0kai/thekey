package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.wrap.AsyncCoroutineProvide
import com.github.klee0kai.thekey.app.engine.FindStorageEngine

interface EngineDependencies {

    fun findStorageEngineLazy(): AsyncCoroutineProvide<FindStorageEngine>

}