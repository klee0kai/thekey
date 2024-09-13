package com.github.klee0kai.thekey.app.paparazzi.designkit

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields.DropDownFieldPreview
import org.junit.Test

class DropDownFieldScreens : BasePaparazzi() {

    @Test
    fun fabSimplePreview() {
        paparazzi.snapshot {
            DropDownFieldPreview()
        }
    }

}