package com.github.klee0kai.thekey.core.utils.coroutine

import androidx.lifecycle.AtomicReference
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch


interface TouchableFlow<out T, in Arg> : Flow<T> {

    /**
     * update data in cold flow
     */
    fun touch(arg: Arg)

}

fun <T> TouchableFlow<T, Unit>.touch() = touch(Unit)

fun <T> Flow<T>.touchable(): TouchableFlow<T, Unit> {
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
    return object : TouchableFlow<T, Unit>, Flow<T> by touchBody {

        override fun touch(arg: Unit) {
            ticker.tryEmit(arg)
        }

    }
}

fun <T, Arg> touchableFlow(
    init: Arg,
    block: suspend FlowCollector<T>.(arg: Arg) -> Unit,
): TouchableFlow<T, Arg> {
    val ticker = MutableSharedFlow<Arg>(replay = 1)
    val touchBody = channelFlow {
        val lastJob = AtomicReference<Job?>()
        ticker.onTicks(init) { arg ->
            lastJob.getAndUpdate { last ->
                last?.cancel()
                launch {
                    flow<T> {
                        block(arg)
                    }.collect {
                        send(it)
                    }
                }
            }
        }
    }
    return object : TouchableFlow<T, Arg>, Flow<T> by touchBody {

        override fun touch(arg: Arg) {
            ticker.tryEmit(arg)
        }

    }
}