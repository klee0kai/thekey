package com.github.klee0kai.thekey.app.paparazzi.navigationboard

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.selectstorageboard.SelectStorageToNoteMoveBoardPreview
import org.junit.Test

class SelectStorageToNoteMoveBoardScreens : BasePaparazzi() {

    @Test
    fun selectStorageToNoteMoveBoardPreview() {
        paparazzi.snapshot {
            SelectStorageToNoteMoveBoardPreview()
        }
    }

}