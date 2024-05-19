package com.github.klee0kai.thekey.app.paparazzi.designkit

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.designkit.text.AppTextEmptyFieldPreview
import com.github.klee0kai.thekey.app.ui.designkit.text.AppTextFieldPreview
import org.junit.Test

class AppTextFieldScreens : BasePaparazzi() {

    @Test
    fun sTextFieldPreview() {
        paparazzi.snapshot {
            AppTextFieldPreview()
        }
    }

    @Test
    fun sTextEmptyFieldPreview() {
        paparazzi.snapshot {
            AppTextEmptyFieldPreview()
        }
    }

}