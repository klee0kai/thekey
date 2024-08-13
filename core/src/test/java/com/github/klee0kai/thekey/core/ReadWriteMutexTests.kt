package com.github.klee0kai.thekey.core

import com.github.klee0kai.thekey.core.utils.common.ReadWriteMutex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.random.Random

class ReadWriteMutexTests {

    @Test
    fun listMassiveUseTest() {
        val mutex = ReadWriteMutex()
        val mutableList = mutableListOf<Int>()

        repeat(Random.nextInt(100)) {
            mutableList.add(Random.nextInt())
        }

        repeat(100) { writeRatio ->
            runBlocking {
                repeat(1000) {
                    val readFear = Random.nextInt(1)
                    val writeFear = Random.nextInt(writeRatio + 1)

                    launch(Dispatchers.IO) {
                        if (writeFear > readFear) {
                            mutex.withWriteLock {
                                mutableList.clear()
                                repeat(Random.nextInt(100) + 2) {
                                    mutableList.add(Random.nextInt())
                                }
                                mutableList.clear()
                                repeat(Random.nextInt(100) + 2) {
                                    mutableList.add(Random.nextInt())
                                }
                            }
                        } else {
                            mutex.withReadLock {
                                val sum = mutableList.sum()
                                val average = mutableList.average()
                                val max = mutableList.max()
                                val min = mutableList.min()
                            }
                        }
                    }
                }
            }

        }
    }

}