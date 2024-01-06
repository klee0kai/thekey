package com.github.klee0kai.thekey.app.mocks.helpers

import com.github.klee0kai.thekey.app.utils.path.ShortPath
import com.github.klee0kai.thekey.app.utils.path.UserShortPaths

class MocksUserShortPaths : UserShortPaths() {

    override val appData by lazy {
        ShortPath("appdata", "/app/thekey/data")
    }

    override val phoneStorage by lazy {
        ShortPath(
            "phoneStorage",
            "/storage/emulated/0"
        )
    }
}