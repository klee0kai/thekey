package com.github.klee0kai.thekey.app.paparazzi.designkit

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.core.ui.devkit.components.FabSimpleInContainerPreview
import com.github.klee0kai.thekey.core.ui.devkit.components.FabSimplePreview
import org.junit.Test

class FabSimpleScreens : BasePaparazzi() {

    @Test
    fun fabSimplePreview() {
        paparazzi.snapshot {
            FabSimplePreview()
        }
    }

    @Test
    fun fabSimpleInContainerPreview() {
        paparazzi.snapshot {
            FabSimpleInContainerPreview()
        }
    }

}