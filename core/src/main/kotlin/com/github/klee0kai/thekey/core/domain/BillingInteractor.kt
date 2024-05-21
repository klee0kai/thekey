package com.github.klee0kai.thekey.core.domain

import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature

open class BillingInteractor {

   open fun isAvailable(feature: DynamicFeature): Boolean = false

}