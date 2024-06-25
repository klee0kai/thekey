package com.github.klee0kai.thekey.core.utils.coroutine

import androidx.lifecycle.AtomicReference
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch


interface TouchableFlow<out T> : Flow<T> {

    /**
     * update data in cold flow
     */
    fun touch()

}

fun <T> Flow<T>.touchable(): TouchableFlow<T> {
    val originFlow = this
    val ticker = MutableSharedFlow<Unit>(replay = 1)
    val touchBody = channelFlow {
        val lastJob = AtomicReference<Job?>()
        ticker.onTicks {
            lastJob.getAndUpdate { last ->
                last?.cancel()
                launch {
                    originFlow.collect {
                        send(it)
                    }
                }
            }
        }
    }
    return object : TouchableFlow<T>, Flow<T> by touchBody {
        override fun touch() {
            ticker.tryEmit(Unit)
        }

    }
}