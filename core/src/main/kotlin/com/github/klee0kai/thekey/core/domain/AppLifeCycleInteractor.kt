package com.github.klee0kai.thekey.core.domain

interface AppLifeCycleInteractor {

    fun appStarted() = Unit

    fun appMinimazed() = Unit

}