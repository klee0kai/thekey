package com.github.klee0kai.thekey.dynamic.findstorage.tests.filesystem

import com.github.klee0kai.thekey.core.utils.file.appendPrefix
import com.github.klee0kai.thekey.core.utils.file.appendSuffix
import com.github.klee0kai.thekey.core.utils.file.parents
import com.github.klee0kai.thekey.core.utils.file.removeFileExtension
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File

class FileExtTests {

    @Test
    fun listParents() = runBlocking {
        val parents = File("/some/folder/file.txt").parents.toList()

        assertEquals(
            listOf(
                File("/some/folder/file.txt"),
                File("/some/folder"),
                File("/some"),
                File("/"),
            ),
            parents,
        )
    }

    @Test
    fun appendPrefix() = runBlocking {
        assertEquals(
            "/some/",
            "/some/".appendPrefix("/"),
        )
    }

    @Test
    fun appendPrefix2() = runBlocking {
        assertEquals(
            "/some/",
            "some/".appendPrefix("/"),
        )
    }

    @Test
    fun appendPrefix3() = runBlocking {
        assertEquals(
            "/some",
            "some".appendPrefix("/"),
        )
    }

    @Test
    fun appendSuffix() = runBlocking {
        assertEquals(
            "/some/",
            "/some/".appendSuffix("/"),
        )
    }

    @Test
    fun appendSuffix2() = runBlocking {
        assertEquals(
            "/some/",
            "/some".appendSuffix("/"),
        )
    }


    @Test
    fun removeFileExt() = runBlocking {
        assertEquals(
            "/some/folder/file",
            "/some/folder/file.zp.txt".removeFileExtension(),
        )
    }


    @Test
    fun removeFileExt2() = runBlocking {
        assertEquals(
            "/some/folder.ex/file",
            "/some/folder.ex/file.zp.txt".removeFileExtension(),
        )
    }

    @Test
    fun removeFileExt4() = runBlocking {
        assertEquals(
            "file",
            "file.zp.txt".removeFileExtension(),
        )
    }

    @Test
    fun removeFileExt5() = runBlocking {
        assertEquals(
            "file",
            "file".removeFileExtension(),
        )
    }

    @Test
    fun removeFileExt6() = runBlocking {
        assertEquals(
            "",
            "".removeFileExtension(),
        )
    }


}