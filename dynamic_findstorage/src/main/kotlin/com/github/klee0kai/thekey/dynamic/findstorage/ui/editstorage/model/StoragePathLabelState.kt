package com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface StoragePathLabelState : Parcelable {

    /**
     * just write storage path
     */
    @Parcelize
    data object Simple : StoragePathLabelState

    /**
     * Moving storage path
     */
    @Parcelize
    data object MovingStoragePath : StoragePathLabelState

    /**
     * Moving storage path
     */
    @Parcelize
    data object CreateStoragePath : StoragePathLabelState

}