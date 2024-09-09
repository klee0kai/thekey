package com.github.klee0kai.thekey.app.engine.editstorage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.ChPasswStrategy
import com.github.klee0kai.thekey.app.engine.model.CreateStorageConfig
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.core.di.identifiers.FileIdentifier
import com.github.klee0kai.thekey.core.domain.model.DebugConfigs
import com.github.klee0kai.thekey.core.utils.error.FSNoAccessError
import com.github.klee0kai.thekey.core.utils.error.FSNoFileName
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.IOException

class EditStorageSuspended {

    private val _engine = DI.editStorageEngineLazy()
    private val dispatcher = DI.jniDispatcher()

    suspend fun findStorageInfo(path: String): Storage? = engineRead(path) {
        findStorageInfo(path)
    }

    suspend fun createStorage(
        storage: Storage,
        createStorageConfig: CreateStorageConfig,
    ): Int = engineWrite(storage.path) {
        createStorage(storage, createStorageConfig)
    }

    suspend fun editStorage(storage: Storage): Int = engineWrite(storage.path) {
        editStorage(storage)
    }

    suspend fun move(from: String, to: String): Int {
        if (from == to) {
            return engineWrite(to) {
                move(from, to)
            }
        }
        return engineRead(from) {
            engineWrite(to) {
                move(from, to)
            }
        }
    }

    suspend fun changePassw(
        path: String,
        currentPassw: String,
        newPassw: String,
    ) = engineWrite(path) {
        changePassw(path, currentPassw, newPassw)
    }

    suspend fun changePasswStrategy(path: String, strategies: List<ChPasswStrategy>) =
        engineWrite(path) {
            changePasswStrategy(path, strategies.toTypedArray())
        }


    suspend fun notes(
        path: String,
        passw: String,
    ): Array<DecryptedNote> = engineRead(path) {
        notes(path, passw)
    }

    suspend fun otpNotes(
        path: String,
        passw: String,
    ): Array<DecryptedOtpNote> = engineRead(path) {
        otpNotes(path, passw)
    }

    fun throwError(code: Int) = when (code) {
        0 -> Unit
        -2 -> throw FSNoFileName()
        -3 -> throw FSNoAccessError()
        else -> throw IOException()
    }

    private suspend fun <T> engineWrite(
        path: String,
        block: suspend EditStorageEngine.() -> T,
    ): T = withContext(dispatcher) {
        val fileMutex = DI.fileMutex(FileIdentifier(path))
        fileMutex.withWriteLock {
            delay(DebugConfigs.engineDelay.writeDelay)
            _engine().block()
        }
    }

    private suspend fun <T> engineRead(
        path: String,
        block: suspend EditStorageEngine.() -> T,
    ): T = withContext(dispatcher) {
        val fileMutex = DI.fileMutex(FileIdentifier(path))
        fileMutex.withReadLock {
            delay(DebugConfigs.engineDelay.readDelay)
            _engine().block()
        }
    }
}

