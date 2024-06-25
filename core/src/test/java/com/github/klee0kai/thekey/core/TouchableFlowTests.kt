package com.github.klee0kai.thekey.core

import com.github.klee0kai.thekey.core.utils.coroutine.touchable
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test

class TouchableFlowTests {

    @Test
    fun updateOnTouch() = runBlocking {
        var triggersCount = 0
        val someFlow = flow {
            emit(triggersCount++)
            delay(Long.MAX_VALUE)
        }.touchable()

        var lastValue = -1
        val subscriber = launch {
            someFlow.collect {
                lastValue = it
            }
        }
        delay(10)
        someFlow.touch()
        delay(10)
        someFlow.touch()

        delay(10)
        assertEquals(3, triggersCount)
        assertEquals(2, lastValue)

        subscriber.cancel()
    }


    @Test
    fun simpleFlowWork() = runBlocking {
        val someFlow = flow {
            emit(1)
            emit(2)
            emit(3)
        }.touchable()

        var lastValue = -1
        val subscriber = launch {
            someFlow.collect {
                lastValue = it
            }
        }
        delay(100)
        assertEquals(3, lastValue)
        subscriber.cancel()
    }


}