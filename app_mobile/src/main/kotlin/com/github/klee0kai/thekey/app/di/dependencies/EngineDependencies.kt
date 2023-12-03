package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.stone.wrappers.AsyncProvide
import com.github.klee0kai.thekey.app.engine.FindStorageEngine

interface EngineDependencies {

    fun findStorageEngineLazy(): AsyncProvide<FindStorageEngine>

}