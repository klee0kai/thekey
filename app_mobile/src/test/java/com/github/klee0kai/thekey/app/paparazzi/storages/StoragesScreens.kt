package com.github.klee0kai.thekey.app.paparazzi.storages

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.storages.StoragesScreenPreview
import org.junit.Test

class StoragesScreens : BasePaparazzi() {

    @Test
    fun storagesScreenPreview() {
        paparazzi.snapshot {
            StoragesScreenPreview()
        }
    }
}