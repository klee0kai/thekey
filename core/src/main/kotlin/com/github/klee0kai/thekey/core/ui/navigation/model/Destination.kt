package com.github.klee0kai.thekey.core.ui.navigation.model

import android.os.Parcelable
import androidx.compose.runtime.Stable
import com.github.klee0kai.thekey.core.feature.model.DynamicFeature

@Stable
interface Destination : Parcelable

@Stable
abstract class DynamicDestination(
    val feature: DynamicFeature
) : Destination


@Stable
interface DialogDestination : Destination