package com.github.klee0kai.thekey.app.helpers

import com.github.klee0kai.thekey.app.TargetDI
import com.github.klee0kai.thekey.app.di.DI
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class UserShortPathsTests {

    private val helper by lazy { DI.userShortPaths() }


    @Before
    fun init() {
        with(TargetDI) { DI.initDummyModules() }
    }


    @Test
    fun emptyAbsolutePathTest() = runBlocking {
        assertEquals("", helper.absolutePath(""))
    }

    @Test
    fun emptyShortPathTest() = runBlocking {
        assertEquals("", helper.shortPathName(""))
    }


    @Test
    fun appStoragePathTest1() = runBlocking {
        assertEquals("/app/thekey/data", helper.absolutePath("appdata"))
    }


    @Test
    fun appStoragePathTest2() = runBlocking {
        assertEquals("/app/thekey/data", helper.absolutePath("/appdata"))
    }

    @Test
    fun appStoragePathTest3() = runBlocking {
        assertEquals("/app/thekey/data/", helper.absolutePath("/appdata/"))
    }


    @Test
    fun shortPathTest1() = runBlocking {
        assertEquals("/appdata", helper.shortPathName("/app/thekey/data"))
    }

}