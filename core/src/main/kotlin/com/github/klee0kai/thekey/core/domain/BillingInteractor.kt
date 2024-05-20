package com.github.klee0kai.thekey.core.domain

import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature

interface BillingInteractor {

    fun isAvailable(feature: DynamicFeature): Boolean = false

}