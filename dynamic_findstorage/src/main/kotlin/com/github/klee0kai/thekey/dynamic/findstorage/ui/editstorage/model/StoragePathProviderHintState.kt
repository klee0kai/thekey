package com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model

import android.os.Parcelable
import com.github.klee0kai.thekey.dynamic.findstorage.domain.model.FileItem
import kotlinx.parcelize.Parcelize

sealed interface StoragePathProviderHintState : Parcelable {

    /**
     * Available storage in folder
     */
    @Parcelize
    data object Empty : StoragePathProviderHintState

    /**
     * Available storage in folder
     */
    @Parcelize
    data object AvailableStorages : StoragePathProviderHintState

    /**
     * Available storage in folder
     */
    @Parcelize
    data class CreateFolderFrom(
        val parentFolder: FileItem,
    ) : StoragePathProviderHintState


}