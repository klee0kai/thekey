package com.github.klee0kai.thekey.app.paparazzi.designkit

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.designkit.text.STextEmptyFieldPreview
import com.github.klee0kai.thekey.app.ui.designkit.text.STextFieldPreview
import org.junit.Test

class AppTextFieldScreens : BasePaparazzi() {

    @Test
    fun sTextFieldPreview() {
        paparazzi.snapshot {
            STextFieldPreview()
        }
    }

    @Test
    fun sTextEmptyFieldPreview() {
        paparazzi.snapshot {
            STextEmptyFieldPreview()
        }
    }

}