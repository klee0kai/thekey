package com.github.klee0kai.thekey.app.ui.navigation.model

import com.github.klee0kai.thekey.core.domain.model.Subscription
import com.github.klee0kai.thekey.core.ui.navigation.model.CommercialDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import kotlinx.parcelize.Parcelize

@Parcelize
data class SubscriptionsDestination(
    val subscriptionToBuy: Subscription? = null,
) : CommercialDestination, Destination