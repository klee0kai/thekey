package com.github.klee0kai.thekey.app.helpers

import androidx.compose.ui.text.input.TextFieldValue
import com.github.klee0kai.thekey.app.TargetDI
import com.github.klee0kai.thekey.app.di.DI
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PathInputHelperTests {

    private val helper by lazy { DI.pathInputHelper() }

    @Before
    fun init() {
        with(TargetDI) { DI.initDummyModules() }
    }


    @Test
    fun emptyInputTest() = runBlocking {
        with(helper) {
            assertEquals(
                "",
                TextFieldValue("").pathInputMask().text
            )
        }

    }


    @Test
    fun rootPathTest() = runBlocking {
        with(helper) {
            assertEquals(
                "/someFolder",
                TextFieldValue("someFolder")
                    .pathInputMask()
                    .text
            )
        }
    }


    @Test
    fun somePathTest() = runBlocking {
        with(helper) {
            assertEquals(
                "/someFolder/child",
                TextFieldValue("someFolder/child")
                    .pathInputMask()
                    .text
            )
        }

    }

    @Test
    fun dirPathTest() = runBlocking {
        with(helper) {
            assertEquals(
                "/someFolder/child/",
                TextFieldValue("someFolder/child/")
                    .pathInputMask()
                    .text
            )
        }

    }

}