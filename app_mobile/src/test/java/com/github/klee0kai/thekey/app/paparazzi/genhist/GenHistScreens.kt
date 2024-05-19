package com.github.klee0kai.thekey.app.paparazzi.genhist

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.genhist.GenHistScreenPreview
import org.junit.Test

class GenHistScreens : BasePaparazzi() {

    @Test
    fun genHistScreenPreview() {
        paparazzi.snapshot {
            GenHistScreenPreview()
        }
    }

}