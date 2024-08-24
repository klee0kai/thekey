package com.github.klee0kai.thekey.core.utils.common

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Copyright 2017 ModelBox Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * The read or write mode of the [ReadWriteMutex].
 */
enum class MutexMode {
    READ,
    WRITE
}

/**
 * The lock state of the [ReadWriteMutex].
 */
enum class MutexState {
    /**
     * The mutex has been locked.
     */
    LOCKED,

    /**
     * The mutex has been unlocked.
     */
    UNLOCKED
}

data class MutexInfo(val mode: MutexMode, val state: MutexState)


class ReadWriteMutex {
    /**
     * A mutex to guard the creation of new readers.
     */
    private val allowNewReads = Mutex()

    /**
     * A mutex to guard the creation of new writers.
     */
    private val allowNewWrites = Mutex()
    private var readers = 0

    /**
     * Controls access to [readers].
     */
    private val stateLock = Mutex()

    private val stateListenersMutex = Mutex()
    private val stateListeners = mutableListOf<suspend (MutexInfo) -> Unit>()

    var state = MutexInfo(MutexMode.READ, MutexState.UNLOCKED)
        private set

    suspend fun <T> withReadLock(block: suspend () -> T): T {
        return try {
            // Ensure new readers are allowed
            allowNewReads.withLock {
                stateLock.withLock {
                    // Increment the reader count.
                    if (readers++ == 0) {
                        // If we're the first reader, ensure that writes are locked out
                        allowNewWrites.lock(this)
                        // Invoke user callback
                        state = MutexInfo(MutexMode.READ, MutexState.LOCKED)
                        notifyListeners()
                    }
                }
            }
            // Execute the user function.
            block()
        } finally {
            // We don't want to use allowNewReads here, because a writer can acquire that lock
            // while waiting fol allowNewWrites to be unlocked.  Instead, we'll just treat clean-up
            // like we're draining all outstanding readers to admit the writer.
            stateLock.withLock {
                // Decrement the reader count, and unlock if we were the last reader
                if (--readers == 0) {
                    try {
                        // Invoke user callback in opposite order from above
                        state = MutexInfo(MutexMode.READ, MutexState.UNLOCKED)
                        notifyListeners()
                    } finally {
                        // If a writer is pending, this will unlock it
                        allowNewWrites.unlock(this)
                    }
                }
            }
        }
    }

    suspend fun <T> withWriteLock(fn: suspend () -> T): T {
        // Prevent readers from starting any new action
        return allowNewReads.withLock {
            // Wait for all outstanding readers to drain.
            allowNewWrites.withLock {
                try {
                    state = MutexInfo(MutexMode.WRITE, MutexState.LOCKED)
                    notifyListeners()
                    fn()
                } finally {
                    state = MutexInfo(MutexMode.WRITE, MutexState.UNLOCKED)
                    notifyListeners()
                }
            }
        }
    }

    suspend fun subscribe(listener: suspend (MutexInfo) -> Unit) = stateListenersMutex.withLock {
        stateListeners.add(listener)
    }

    suspend fun unsubscribe(listener: suspend (MutexInfo) -> Unit) = stateListenersMutex.withLock {
        stateListeners.remove(listener)
    }

    private suspend fun notifyListeners(): Unit = stateListenersMutex.withLock {
        stateListeners.forEach { listener ->
            listener.invoke(state)
        }
    }

}

fun ReadWriteMutex.stateFlow() = channelFlow<MutexInfo> {
    val listener: suspend (MutexInfo) -> Unit = {
        send(it)
    }

    subscribe(listener)
    listener.invoke(state)
    awaitClose {
        runBlocking { unsubscribe(listener) }
    }

}