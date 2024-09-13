package com.github.klee0kai.thekey.app.paparazzi.designkit

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields.ColorGroupDropDownFieldPreview
import org.junit.Test

class ColorGroupDropDownFieldScreens : BasePaparazzi() {

    @Test
    fun fabSimplePreview() {
        paparazzi.snapshot {
            ColorGroupDropDownFieldPreview()
        }
    }

}