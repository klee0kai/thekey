package com.github.klee0kai.thekey.core.di.dependecies

import com.github.klee0kai.thekey.core.domain.BillingInteractor
import com.github.klee0kai.thekey.core.domain.StartupInteractor

interface CoreInteractorsDependencies {

    fun billingInteractor(): BillingInteractor

    fun startupInteractor(): StartupInteractor

}