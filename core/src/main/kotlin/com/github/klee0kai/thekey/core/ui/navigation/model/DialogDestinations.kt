package com.github.klee0kai.thekey.core.ui.navigation.model

import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.domain.model.Subscription
import kotlinx.parcelize.Parcelize

@Parcelize
data class SimpleDialogDestination(
    val title: TextProvider = TextProvider(R.string.title),
    val message: TextProvider = TextProvider(R.string.description),
    val confirm: TextProvider = TextProvider(R.string.ok),
    val reject: TextProvider? = null,
) : DialogDestination


@Parcelize
data class SubscriptionsDialogDestination(
    val subscriptionToBuy: Subscription? = null,
) : DialogDestination
