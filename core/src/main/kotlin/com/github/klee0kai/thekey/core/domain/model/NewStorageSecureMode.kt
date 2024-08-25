package com.github.klee0kai.thekey.core.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class NewStorageSecureMode {
    /**
     *  10 iterations are used for encryption.
     */
    @SerialName("low")
    LOW_SECURE,

    /**
     * 1000 iterations are used for encryption.
     */
    @SerialName("middle")
    MIDDLE_SECURE,

    /**
     * Iterations for encryption are 100_000
     */
    @SerialName("hard")
    HARD_SECURE,
}


fun NewStorageSecureMode.nextRecursive(): NewStorageSecureMode = when (this) {
    NewStorageSecureMode.LOW_SECURE -> NewStorageSecureMode.MIDDLE_SECURE
    NewStorageSecureMode.MIDDLE_SECURE -> NewStorageSecureMode.HARD_SECURE
    NewStorageSecureMode.HARD_SECURE -> NewStorageSecureMode.LOW_SECURE
}
