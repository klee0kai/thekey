package com.github.klee0kai.thekey.app.paparazzi.designkit

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.core.ui.devkit.components.buttons.AddCirclePreview
import com.github.klee0kai.thekey.core.ui.devkit.components.buttons.GroupCircleCheckedPreview
import com.github.klee0kai.thekey.core.ui.devkit.components.buttons.GroupCirclePreview
import org.junit.Test

class ButtonsScreens : BasePaparazzi() {

    @Test
    fun addCirclePreview() {
        paparazzi.snapshot {
            AddCirclePreview()
        }
    }

    @Test
    fun groupCirclePreview() {
        paparazzi.snapshot {
            GroupCirclePreview()
        }
    }

    @Test
    fun groupCircleCheckedPreview() {
        paparazzi.snapshot {
            GroupCircleCheckedPreview()
        }
    }


}