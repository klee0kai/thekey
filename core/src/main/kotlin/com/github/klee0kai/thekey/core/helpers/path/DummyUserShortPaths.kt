package com.github.klee0kai.thekey.core.helpers.path

class DummyUserShortPaths : UserShortPaths() {

    override val appPath: String by lazy { "/app/thekey/data" }

    override val shortPaths = listOf(
        ShortPath("appdata", "/app/thekey/data"),
        ShortPath("phoneStorage", "/storage/emulated/0")
    )

}