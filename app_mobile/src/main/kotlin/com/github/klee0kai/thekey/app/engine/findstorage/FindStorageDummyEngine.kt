package com.github.klee0kai.thekey.app.engine.findstorage

class FindStorageDummyEngine : FindStorageSuspended() {

    override suspend fun findStorages(folder: String, listener: FindStorageListener) = Unit

}