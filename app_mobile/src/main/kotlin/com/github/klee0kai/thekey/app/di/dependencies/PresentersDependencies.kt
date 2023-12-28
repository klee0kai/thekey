package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.ui.editstorage.CreateStoragePresenter
import com.github.klee0kai.thekey.app.ui.login.LoginPresenter
import com.github.klee0kai.thekey.app.ui.storages.StoragesPresenter

interface PresentersDependencies {

    fun loginPresenter(): LoginPresenter

    fun storagesPresenter(): StoragesPresenter

    fun editStoragePresenter(identifier: StorageIdentifier): CreateStoragePresenter

}