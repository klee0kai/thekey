package com.github.klee0kai.thekey.app.paparazzi.navigationboard

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.simpleboard.SimpleBoardPreview
import org.junit.Test

class SimpleBoardScreens : BasePaparazzi() {

    @Test
    fun storageNavigationBoardPreview() {
        paparazzi.snapshot {
            SimpleBoardPreview()
        }
    }

}