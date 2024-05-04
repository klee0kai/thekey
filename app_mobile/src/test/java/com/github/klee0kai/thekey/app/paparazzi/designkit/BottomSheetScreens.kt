package com.github.klee0kai.thekey.app.paparazzi.designkit

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.designkit.components.bottomsheet.SimpleBottomSheetScaffoldPreview
import org.junit.Test

class BottomSheetScreens : BasePaparazzi() {

    @Test
    fun simpleBottomSheetScaffoldPreview() {
        paparazzi.snapshot {
            SimpleBottomSheetScaffoldPreview()
        }
    }


}