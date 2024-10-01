package com.github.klee0kai.thekey.core.domain.model.feature.model

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class DynamicFeature(
    val moduleName: String = "",
    @StringRes val titleRes: Int = 0,
    @StringRes val descRes: Int = 0,
    val featureLibApiClass: String? = null,
    val purchase: String = "",
    val isCommunity: Boolean = false,
    /**
     * Hidden in plugin list
     */
    val isHidden: Boolean = false,
) : Parcelable {
    companion object;
}

