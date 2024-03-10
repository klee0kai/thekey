package com.github.klee0kai.thekey.app.ui.navigation.model

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import com.github.klee0kai.thekey.app.R
import kotlinx.parcelize.Parcelize

@Stable
interface DialogDestination : Destination

@Parcelize
data class AlertDialogDestination(
    val title: TextProvider = TextProvider(R.string.title),
    val message: TextProvider = TextProvider(R.string.description),
    val confirm: TextProvider = TextProvider(R.string.ok),
    val reject: TextProvider? = TextProvider(R.string.cancel),
    @DrawableRes val iconRes: Int? = null,
) : DialogDestination