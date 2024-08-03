package com.github.klee0kai.thekey.app.engine.editstorage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.core.utils.error.FSNoAccessError
import com.github.klee0kai.thekey.core.utils.error.FSNoFileName
import kotlinx.coroutines.withContext
import java.io.IOException

class EditStorageSuspended {

    private val _engine = DI.editStorageEngineLazy()
    private val dispatcher = DI.jniDispatcher()

    suspend fun findStorageInfo(path: String): Storage? = engineRun { findStorageInfo(path) }

    suspend fun createStorage(storage: Storage): Int = engineRun { createStorage(storage) }

    suspend fun editStorage(storage: Storage): Int = engineRun { editStorage(storage) }

    suspend fun move(from: String, to: String): Int = engineRun { move(from, to) }

    suspend fun changePassw(path: String, currentPassw: String, newPassw: String) =
        engineRun { changePassw(path, currentPassw, newPassw) }

    suspend fun notes(path: String, passw: String): Array<DecryptedNote> =
        engineRun { notes(path, passw) }

    suspend fun otpNotes(path: String, passw: String): Array<DecryptedOtpNote> =
        engineRun { otpNotes(path, passw) }

    fun throwError(code: Int) = when (code) {
        0 -> Unit
        -2 -> throw FSNoFileName()
        -3 -> throw FSNoAccessError()
        else -> throw IOException()
    }

    private suspend fun <T> engineRun(block: suspend EditStorageEngine.() -> T): T =
        withContext(dispatcher) {
            _engine().block()
        }
}

