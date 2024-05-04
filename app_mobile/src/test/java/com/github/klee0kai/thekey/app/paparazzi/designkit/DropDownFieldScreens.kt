package com.github.klee0kai.thekey.app.paparazzi.designkit

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.designkit.components.dropdownfields.DropDownFieldEmptyPreview
import com.github.klee0kai.thekey.app.ui.designkit.components.dropdownfields.DropDownFieldExpandedPreview
import com.github.klee0kai.thekey.app.ui.designkit.components.dropdownfields.DropDownFieldSelectedPreview
import org.junit.Test

class DropDownFieldScreens : BasePaparazzi() {

    @Test
    fun dropDownFieldEmptyPreview() {
        paparazzi.snapshot {
            DropDownFieldEmptyPreview()
        }
    }

    @Test
    fun dropDownFieldSelectedPreview() {
        paparazzi.snapshot {
            DropDownFieldSelectedPreview()
        }
    }

    @Test
    fun dropDownFieldExpandedPreview() {
        paparazzi.snapshot {
            DropDownFieldExpandedPreview()
        }
    }

}