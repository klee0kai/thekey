package com.github.klee0kai.thekey.app.engine.model

import android.os.Parcelable
import com.github.klee0kai.brooklyn.JniPojo
import com.github.klee0kai.thekey.core.domain.model.NewStorageSecureMode
import kotlinx.parcelize.Parcelize

@JniPojo
@Parcelize
data class CreateStorageConfig(
    val keyInteractionsCount: Long = 0L,
    val interactionsCount: Long = 0L,
) : Parcelable

fun NewStorageSecureMode.createConfig() = when (this) {
    NewStorageSecureMode.LOW_SECURE -> CreateStorageConfig(
        keyInteractionsCount = 1_000,
        interactionsCount = 100,
    )

    NewStorageSecureMode.MIDDLE_SECURE -> CreateStorageConfig(
        keyInteractionsCount = 10_000,
        interactionsCount = 1_000,
    )

    NewStorageSecureMode.HARD_SECURE -> CreateStorageConfig(
        keyInteractionsCount = 100_000,
        interactionsCount = 10_000,
    )
}