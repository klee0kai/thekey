package com.github.klee0kai.thekey.app.engine

import java.lang.IllegalStateException

class StorageVersionNotSupported(
    message: String? = null,
    cause: Throwable? = null,
) : IllegalStateException(message, cause)