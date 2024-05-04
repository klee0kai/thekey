package com.github.klee0kai.thekey.app.ui.storage.genpassw.presenter

import com.github.klee0kai.thekey.app.ui.storage.genpassw.model.GenPasswState
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

interface GenPasswPresenter {

    val passwLenRange get() = (4..16)

    val state: Flow<GenPasswState> get() = emptyFlow()

    fun init(): Job = Job()

    fun generatePassw(): Job = Job()

    fun copyToClipboard(): Job = Job()

    fun saveAsNewNote(): Job = Job()

    fun input(block: GenPasswState.() -> GenPasswState): Job = Job()
}