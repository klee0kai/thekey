package com.github.klee0kai.thekey.app.helpers

import com.github.klee0kai.thekey.app.helpers.path.ShortPath
import com.github.klee0kai.thekey.app.helpers.path.UserShortPaths

class DummyUserShortPaths : UserShortPaths() {

    override val shortPaths = listOf(
        ShortPath("appdata", "/app/thekey/data"),
        ShortPath("phoneStorage", "/storage/emulated/0")
    )

}