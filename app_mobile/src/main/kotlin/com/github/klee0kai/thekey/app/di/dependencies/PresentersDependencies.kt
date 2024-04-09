package com.github.klee0kai.thekey.app.di.dependencies

import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.ui.editstorage.CreateStoragePresenter
import com.github.klee0kai.thekey.app.ui.genhist.GenHistPresenter
import com.github.klee0kai.thekey.app.ui.login.presenter.LoginPresenter
import com.github.klee0kai.thekey.app.ui.note.presenter.EditNotePresenter
import com.github.klee0kai.thekey.app.ui.notegroup.EditNoteGroupPresenter
import com.github.klee0kai.thekey.app.ui.storage.StoragePresenter
import com.github.klee0kai.thekey.app.ui.storage.genpassw.GenPasswPresenter
import com.github.klee0kai.thekey.app.ui.storages.StoragesPresenter

interface PresentersDependencies {

    fun loginPresenter(): LoginPresenter

    fun storagesPresenter(): StoragesPresenter

    fun editStoragePresenter(identifier: StorageIdentifier? = null): CreateStoragePresenter

    fun storagePresenter(identifier: StorageIdentifier): StoragePresenter

    fun genPasswPresenter(identifier: StorageIdentifier): GenPasswPresenter

    fun genHistPresenter(identifier: StorageIdentifier): GenHistPresenter

    fun editNotePresenter(noteIdentifier: NoteIdentifier): EditNotePresenter

    fun editNoteGroupPresenter(id: NoteGroupIdentifier): EditNoteGroupPresenter

}