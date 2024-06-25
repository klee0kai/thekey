package com.github.klee0kai.thekey.app.helpers

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.debug.DebugDI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

@OptIn(DebugOnly::class)
class UserShortPathsTests {

    private val helper by lazy {
        DI.hardResetToPreview()
        DI.userShortPaths()
    }

    @Before
    fun init() {
        with(DebugDI) { DI.initDummyModules() }
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