package com.github.klee0kai.thekey.core.di.modules

import com.github.klee0kai.stone.annotations.module.Module
import com.github.klee0kai.stone.annotations.module.Provide
import com.github.klee0kai.thekey.core.domain.BillingInteractor

@Module
interface CoreInteractorsModule {

    @Provide(cache = Provide.CacheType.Weak)
    fun billingInteractor(): BillingInteractor = BillingInteractor()

}