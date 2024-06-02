package com.github.klee0kai.thekey.core.di.dependecies

import android.content.Context
import androidx.activity.ComponentActivity
import com.github.klee0kai.stone.annotations.dependencies.Dependencies
import com.github.klee0kai.thekey.core.domain.model.AppConfig

@Dependencies
interface CoreDependencyProvider :
    CoreDBDependencies,
    CoreRepositoryDependencies,
    CoreAndroidHelpersDependencies,
    CoreInteractorsDependencies,
    CoroutineDependencies {

    fun ctx(): Context

    fun activity(): ComponentActivity?

    fun config(): AppConfig

}