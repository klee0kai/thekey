package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.ui.editstorage.EditStoragePresenter
import com.github.klee0kai.thekey.app.ui.login.LoginPresenter
import com.github.klee0kai.thekey.app.ui.storages.StoragesPresenter

interface PresentersDependencies {

    fun loginPresenter(): LoginPresenter

    fun storagesPresenter(): StoragesPresenter

    fun editStoragePresenter(): EditStoragePresenter

}