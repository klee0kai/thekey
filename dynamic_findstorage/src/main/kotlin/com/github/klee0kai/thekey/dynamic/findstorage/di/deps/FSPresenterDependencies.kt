package com.github.klee0kai.thekey.dynamic.findstorage.di.deps

import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.presenter.FSEditStoragePresenter
import com.github.klee0kai.thekey.dynamic.findstorage.ui.settings.presenter.FSSettingsPresenter
import com.github.klee0kai.thekey.dynamic.findstorage.ui.storages.presenter.FSStoragesPresenter

interface FSPresenterDependencies {

    fun fsStoragesPresenter(): FSStoragesPresenter

    fun fsEditStoragePresenter(storageIdentifier: StorageIdentifier): FSEditStoragePresenter

    fun fsSettingsPresenter(): FSSettingsPresenter

}