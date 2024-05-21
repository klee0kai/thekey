package com.github.klee0kai.thekey.app.paparazzi.designkit

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.SimpleBottomSheetScaffoldPreview
import org.junit.Test

class BottomSheetScreens : BasePaparazzi() {

    @Test
    fun simpleBottomSheetScaffoldPreview() {
        // errors
        // https://issuetracker.google.com/issues/241895902#comment5
        // https://github.com/cashapp/paparazzi/issues/463
        paparazzi.snapshot {
            SimpleBottomSheetScaffoldPreview()
        }
    }


}