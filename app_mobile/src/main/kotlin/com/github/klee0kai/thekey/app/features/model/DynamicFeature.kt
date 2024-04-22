package com.github.klee0kai.thekey.app.features.model

import android.os.Parcelable
import androidx.annotation.StringRes
import com.github.klee0kai.thekey.app.utils.common.JvmReflection
import kotlinx.parcelize.Parcelize

@Parcelize
data class DynamicFeature(
    val moduleName: String = "",
    @StringRes val titleRes: Int = 0,
    @StringRes val descRes: Int = 0,
    val featureLibApiClass: String? = null,
) : Parcelable {
    companion object;
}

fun DynamicFeature.findApi(): FeatureLibApi? = with(JvmReflection) {
    featureLibApiClass?.let {
        createNew<FeatureLibApi>(it)
    }
}