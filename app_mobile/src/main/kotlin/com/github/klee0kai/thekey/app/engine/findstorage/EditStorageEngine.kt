package com.github.klee0kai.thekey.app.engine.findstorage

import com.github.klee0kai.brooklyn.JniMirror
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.engine.NativeLibLoader
import com.github.klee0kai.thekey.app.engine.model.Storage

@JniMirror
class EditStorageEngine {

    init {
        NativeLibLoader.loadIfNeed()
    }

    external fun findStorageInfo(path: String): Storage?

    external fun createStorage(storage: Storage): Int

    external fun editStorage(storage: Storage): Int

    enum class Error(val code: Int, val stringResId: Int) {
        OK(0, 0),
        UNKNOWN_ERROR(-1, R.string.unknown_error),
        ITIS_FOLDER_ERROR(-2, R.string.fill_the_file_name),
        PATH_UNREACHABLE(-3, R.string.path_unreachable),
        ;

        companion object {
            fun fromCode(code: Int): Error =
                entries
                    .firstOrNull { it.code == code }
                    ?: UNKNOWN_ERROR
        }
    }
}

