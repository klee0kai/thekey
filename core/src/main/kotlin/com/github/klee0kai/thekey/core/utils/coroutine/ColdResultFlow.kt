package com.github.klee0kai.thekey.core.utils.coroutine

import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.launch

interface ColdFlowProduserScope<in E> : ProducerScope<E?> {

    val result: MutableStateFlow<in E?>

}

fun <T> coldStateFlow(block: suspend ColdFlowProduserScope<T>.() -> Unit) = channelFlow<T?> {
    val producer = this
    val coldFlowProduserScope = object : ProducerScope<T?> by producer, ColdFlowProduserScope<T?> {
        override val result = MutableStateFlow<T?>(null)

        init {
            launch {
                result.collect {
                    channel.send(it)
                }
            }
        }
    }
    block.invoke(coldFlowProduserScope)
}
