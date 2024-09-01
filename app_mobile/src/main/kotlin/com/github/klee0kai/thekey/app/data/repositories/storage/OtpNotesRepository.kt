package com.github.klee0kai.thekey.app.data.repositories.storage

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.engine.model.coloredNote
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.domain.model.OtpMethod
import com.github.klee0kai.thekey.core.domain.model.findNextUpdateTime
import com.github.klee0kai.thekey.core.utils.common.launch
import com.github.klee0kai.thekey.core.utils.coroutine.collectTo
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import java.util.concurrent.atomic.AtomicInteger

class OtpNotesRepository(
    val identifier: StorageIdentifier,
) {

    private val engine = DI.cryptStorageEngineSafeLazy(identifier)
    private val scope = DI.defaultThreadScope()
    private val consumers = AtomicInteger(0)
    private val _otpNotes = MutableStateFlow<List<ColoredOtpNote>>(emptyList())
    val otpNotes = channelFlow {
        consumers.incrementAndGet()
        loadOtpNotes()
        _otpNotes.collectTo(this)
        awaitClose { consumers.decrementAndGet() }
    }

    suspend fun otpNote(notePtr: Long) = engine().otpNote(notePtr)

    suspend fun otpNotePinUpdates(
        notePtr: Long,
    ) = channelFlow {
        scope.launch {
            while (isActive) {
                val otp = engine().otpNote(notePtr)
                    .coloredNote(isLoaded = true)
                    .findNextUpdateTime()
                send(otp)
                when (otp.method) {
                    OtpMethod.OTP,
                    OtpMethod.HOTP -> break

                    OtpMethod.TOTP,
                    OtpMethod.YAOTP -> {
                        val now = System.currentTimeMillis()
                        delay(otp.nextUpdateTime - now)
                    }
                }
            }
        }
        awaitClose()
    }

    suspend fun setOtpNotesGroup(notesPtr: List<Long>, groupId: Long) {
        engine().setNotesGroup(notesPtr.toTypedArray(), groupId)
        loadOtpNotes(force = true)
    }

    suspend fun saveOtpNote(note: DecryptedOtpNote, setAll: Boolean = false) {
        engine().saveOtpNote(note, setAll = setAll)
        loadOtpNotes(force = true)
    }

    suspend fun removeOtpNote(noteptr: Long) {
        engine().removeOtpNote(noteptr)
        loadOtpNotes(force = true)
    }

    suspend fun otpNoteFromUrl(url: String): DecryptedOtpNote? {
        return engine().otpNoteFromUrl(url)
    }

    suspend fun clearCache() {
        _otpNotes.update { emptyList() }
    }

    private fun loadOtpNotes(force: Boolean = false) = scope.launch {
        if (_otpNotes.value.isNotEmpty() && !force) return@launch
        if (consumers.get() <= 0) {
            // no consumers
            _otpNotes.value = emptyList()
            return@launch
        }

        if (_otpNotes.value.isEmpty()) {
            _otpNotes.value = engine().otpNotes()
                .map { it.coloredNote(isLoaded = false) }
        }

        _otpNotes.value = engine().otpNotes(info = true)
            .map { it.coloredNote(isLoaded = true) }
    }

}