package com.github.klee0kai.thekey.dynamic.findstorage.tests.filesystem

import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.dynamic.findstorage.data.filesystem.FileSystemRepositoryDummy
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.di.hardResetToPreview
import com.github.klee0kai.thekey.dynamic.findstorage.di.modules.FSRepositoriesModule
import com.github.klee0kai.thekey.dynamic.findstorage.domain.model.FileItem
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(DebugOnly::class)
class ListFilesTests {

    @Test
    fun listRootFiles() = runBlocking {
        // given
        FSDI.hardResetToPreview()
        val interactor = FSDI.fileSystemInteractorLazy()

        // when
        val files = interactor().listFiles("/").await()

        assertEquals(
            listOf(
                FileItem(
                    absPath = "/app/thekey/data",
                    userPath = "/appdata",
                    isFolder = false,
                    isAppInner = true,
                    isExternal = false,
                ),
                FileItem(
                    absPath = "/storage/emulated/0",
                    userPath = "/phoneStorage",
                    isFolder = false,
                    isAppInner = false,
                    isExternal = true
                )
            ),
            files,
        )
    }

    @Test
    fun listRoot2Files() = runBlocking {
        // given
        FSDI.hardResetToPreview()
        FSDI.initFsRepositoriesModule(object : FSRepositoriesModule {
            override fun fsFileSystemRepositoryLazy() = FileSystemRepositoryDummy()
        })
        val interactor = FSDI.fileSystemInteractorLazy()

        // when
        val files = interactor().listFiles().await()

        assertEquals(
            listOf(
                FileItem(
                    absPath = "/app/thekey/data",
                    userPath = "/appdata",
                    isFolder = false,
                    isAppInner = true,
                    isExternal = false,
                ),
                FileItem(
                    absPath = "/storage/emulated/0",
                    userPath = "/phoneStorage",
                    isFolder = false,
                    isAppInner = false,
                    isExternal = true
                ),
            ),
            files,
        )
    }

    @Test
    fun listSinglePhoneStorageFiles() = runBlocking {
        // given
        FSDI.hardResetToPreview()
        FSDI.initFsRepositoriesModule(object : FSRepositoriesModule {
            override fun fsFileSystemRepositoryLazy() = FileSystemRepositoryDummy()
        })
        val interactor = FSDI.fileSystemInteractorLazy()

        // when
        val files = interactor().listFiles("/storage/emulated/0").await()

        assertEquals(
            listOf(
                FileItem(
                    absPath = "/storage/emulated/0",
                    userPath = "/phoneStorage",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
            ),
            files,
        )
    }

    @Test
    fun listSinglePhoneStorage2Files() = runBlocking {
        // given
        FSDI.hardResetToPreview()
        FSDI.initFsRepositoriesModule(object : FSRepositoriesModule {
            override fun fsFileSystemRepositoryLazy() = FileSystemRepositoryDummy()
        })
        val interactor = FSDI.fileSystemInteractorLazy()

        // when
        val files = interactor().listFiles("storage/emulated/0").await()

        assertEquals(
            listOf(
                FileItem(
                    absPath = "/storage/emulated/0",
                    userPath = "/phoneStorage",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
            ),
            files,
        )
    }

    @Test
    fun listPhoneStorageFiles() = runBlocking {
        // given
        FSDI.hardResetToPreview()
        FSDI.initFsRepositoriesModule(object : FSRepositoriesModule {
            override fun fsFileSystemRepositoryLazy() = FileSystemRepositoryDummy()
        })
        val interactor = FSDI.fileSystemInteractorLazy()

        // when
        val files = interactor().listFiles("/storage/emulated/0/").await()

        assertEquals(
            listOf(
                FileItem(
                    absPath = "/storage/emulated/0/Documents",
                    userPath = "/phoneStorage/Documents",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
                FileItem(
                    absPath = "/storage/emulated/0/Downloads",
                    userPath = "/phoneStorage/Downloads",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
                FileItem(
                    absPath = "/storage/emulated/0/Icons",
                    userPath = "/phoneStorage/Icons",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
            ),
            files,
        )
    }

    @Test
    fun listPhoneStorage2Files() = runBlocking {
        // given
        FSDI.hardResetToPreview()
        FSDI.initFsRepositoriesModule(object : FSRepositoriesModule {
            override fun fsFileSystemRepositoryLazy() = FileSystemRepositoryDummy()
        })
        val interactor = FSDI.fileSystemInteractorLazy()

        // when
        val files = interactor().listFiles("storage/emulated/0/").await()

        assertEquals(
            listOf(
                FileItem(
                    absPath = "/storage/emulated/0/Documents",
                    userPath = "/phoneStorage/Documents",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
                FileItem(
                    absPath = "/storage/emulated/0/Downloads",
                    userPath = "/phoneStorage/Downloads",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
                FileItem(
                    absPath = "/storage/emulated/0/Icons",
                    userPath = "/phoneStorage/Icons",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
            ),
            files,
        )
    }


    @Test
    fun listPhoneStorageFilteredFiles() = runBlocking {
        // given
        FSDI.hardResetToPreview()
        FSDI.initFsRepositoriesModule(object : FSRepositoriesModule {
            override fun fsFileSystemRepositoryLazy() = FileSystemRepositoryDummy()
        })
        val interactor = FSDI.fileSystemInteractorLazy()

        // when
        val files = interactor().listFiles("/storage/emulated/0/load").await()

        assertEquals(
            listOf(
                FileItem(
                    absPath = "/storage/emulated/0/Downloads",
                    userPath = "/phoneStorage/Downloads",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                )
            ),
            files,
        )
    }

    @Test
    fun listPhoneStorageFiltered2Files() = runBlocking {
        // given
        FSDI.hardResetToPreview()
        FSDI.initFsRepositoriesModule(object : FSRepositoriesModule {
            override fun fsFileSystemRepositoryLazy() = FileSystemRepositoryDummy()
        })
        val interactor = FSDI.fileSystemInteractorLazy()

        // when
        val files = interactor().listFiles("storage/emulated/0/load").await()

        assertEquals(
            listOf(
                FileItem(
                    absPath = "/storage/emulated/0/Downloads",
                    userPath = "/phoneStorage/Downloads",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                )
            ),
            files,
        )
    }

    @Test
    fun userPathIsWorking() = runBlocking {
        // given
        FSDI.hardResetToPreview()
        FSDI.initFsRepositoriesModule(object : FSRepositoriesModule {
            override fun fsFileSystemRepositoryLazy() = FileSystemRepositoryDummy()
        })
        val interactor = FSDI.fileSystemInteractorLazy()

        // when
        val files = interactor().listFiles("/phoneStorage/").await()

        assertEquals(
            listOf(
                FileItem(
                    absPath = "/storage/emulated/0/Documents",
                    userPath = "/phoneStorage/Documents",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
                FileItem(
                    absPath = "/storage/emulated/0/Downloads",
                    userPath = "/phoneStorage/Downloads",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
                FileItem(
                    absPath = "/storage/emulated/0/Icons",
                    userPath = "/phoneStorage/Icons",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
            ),
            files,
        )
    }


    @Test
    fun listFilesInDownload() = runBlocking {
        // given
        FSDI.hardResetToPreview()
        FSDI.initFsRepositoriesModule(object : FSRepositoriesModule {
            override fun fsFileSystemRepositoryLazy() = FileSystemRepositoryDummy()
        })
        val interactor = FSDI.fileSystemInteractorLazy()

        // when
        val files = interactor().listFiles("/storage/emulated/0/Downloads/some").await()

        assertEquals(
            listOf(
                FileItem(
                    absPath = "/storage/emulated/0/Downloads/some_folder",
                    userPath = "/phoneStorage/Downloads/some_folder",
                    isFolder = true,
                    isAppInner = false,
                    isExternal = true
                ),
                FileItem(
                    absPath = "/storage/emulated/0/Downloads/some_file.txt",
                    userPath = "/phoneStorage/Downloads/some_file.txt",
                    isFolder = false,
                    isAppInner = false,
                    isExternal = true
                ),
            ),
            files,
        )
    }

    @Test
    fun listNotExistFolder() = runBlocking {
        // given
        FSDI.hardResetToPreview()
        FSDI.initFsRepositoriesModule(object : FSRepositoriesModule {
            override fun fsFileSystemRepositoryLazy() = FileSystemRepositoryDummy()
        })
        val interactor = FSDI.fileSystemInteractorLazy()

        // when
        val files = interactor().listFiles("/storage/emulated/0/df/fd").await()

        assertEquals(emptyList<FileItem>(), files)
    }

}