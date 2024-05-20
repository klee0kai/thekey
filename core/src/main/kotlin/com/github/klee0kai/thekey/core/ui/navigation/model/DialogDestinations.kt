package com.github.klee0kai.thekey.core.ui.navigation.model

import androidx.annotation.DrawableRes
import com.github.klee0kai.thekey.core.R
import kotlinx.parcelize.Parcelize

@Parcelize
data class AlertDialogDestination(
    val title: TextProvider = TextProvider(R.string.title),
    val message: TextProvider = TextProvider(R.string.description),
    val confirm: TextProvider = TextProvider(R.string.ok),
    val reject: TextProvider? = TextProvider(R.string.cancel),
    @DrawableRes val iconRes: Int? = null,
) : DialogDestination