package com.github.klee0kai.thekey.app.paparazzi.designkit

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.designkit.components.ListIndicatorHorizontalPreview
import com.github.klee0kai.thekey.app.ui.designkit.components.ListIndicatorVerticalPreview
import com.github.klee0kai.thekey.app.ui.designkit.components.ListIndicatorZeroPreview
import org.junit.Test

class LazyListIndicatorScreens : BasePaparazzi() {

    @Test
    fun listIndicatorVerticalPreview() {
        paparazzi.snapshot {
            ListIndicatorVerticalPreview()
        }
    }

    @Test
    fun listIndicatorHorizontalPreview() {
        paparazzi.snapshot {
            ListIndicatorHorizontalPreview()
        }
    }

    @Test
    fun listIndicatorZeroPreview() {
        paparazzi.snapshot {
            ListIndicatorZeroPreview()
        }
    }

}