package com.github.klee0kai.thekey.app.paparazzi.dialogs

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.core.ui.devkit.dialogs.AlertDialogInScreenPreview
import com.github.klee0kai.thekey.core.ui.devkit.dialogs.AlertDialogPreview
import org.junit.Test

class DialogsScreens : BasePaparazzi() {

    @Test
    fun alertDialogPreview() {
        paparazzi.snapshot {
            AlertDialogPreview()
        }
    }

    @Test
    fun alertDialogInScreenPreview() {
        paparazzi.snapshot {
            AlertDialogInScreenPreview()
        }
    }


}