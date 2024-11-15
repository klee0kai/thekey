package com.github.klee0kai.thekey.core.di.dependecies

import com.github.klee0kai.thekey.core.domain.AppLifeCycleInteractor
import com.github.klee0kai.thekey.core.domain.BillingInteractor

interface CoreInteractorsDependencies {

    fun billingInteractor(): BillingInteractor

    fun lifecycleInteractor(): AppLifeCycleInteractor

}