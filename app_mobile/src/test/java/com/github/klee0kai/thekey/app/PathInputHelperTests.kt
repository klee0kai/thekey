package com.github.klee0kai.thekey.app

import androidx.compose.ui.text.input.TextFieldValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.mocks.MocksHelpersModule
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class PathInputHelperTests {

    private val helper by lazy { DI.pathInputHelper() }

    @Before
    fun init() {
        DI.initHelpersModule(MocksHelpersModule())
    }


    @Test
    fun emptyInputTest() = runBlocking {
        helper
            .autoCompleteVariants(TextFieldValue(""))
            .collect {
                assertEquals("", it.textField.text)
            }
    }


    @Test
    fun rootPathTest() = runBlocking {
        helper
            .autoCompleteVariants(TextFieldValue("someFolder"))
            .collect {
                assertEquals("/someFolder", it.textField.text)
            }
    }


    @Test
    fun somePathTest() = runBlocking {
        helper
            .autoCompleteVariants(TextFieldValue("someFolder/child"))
            .collect {
                assertEquals("/someFolder/child", it.textField.text)
            }
    }

    @Test
    fun dirPathTest() = runBlocking {
        helper
            .autoCompleteVariants(TextFieldValue("someFolder/child/"))
            .collect {
                assertEquals("/someFolder/child/", it.textField.text)
            }
    }

}