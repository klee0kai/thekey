package com.github.klee0kai.thekey.app.paparazzi.editstorage

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.editstorage.EditStorageScreenPreview
import org.junit.Test

class EditStorageScreens : BasePaparazzi() {

    @Test
    fun editStorageScreenPreview() {
        paparazzi.snapshot {
            EditStorageScreenPreview()
        }
    }


}