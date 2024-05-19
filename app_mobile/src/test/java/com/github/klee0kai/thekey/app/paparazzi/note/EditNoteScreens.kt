package com.github.klee0kai.thekey.app.paparazzi.note

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.note.CreateAccountScreenP6Preview
import com.github.klee0kai.thekey.app.ui.note.CreateAccountScreenP6SkeletonPreview
import com.github.klee0kai.thekey.app.ui.note.CreateOTPScreenP6SkeletonPreview
import com.github.klee0kai.thekey.app.ui.note.EditAccountScreenP6Preview
import com.github.klee0kai.thekey.app.ui.note.EditAccountScreenSaveP6Preview
import com.github.klee0kai.thekey.app.ui.note.EditOTPScreenP6Preview
import com.github.klee0kai.thekey.app.ui.note.EditOTPScreenP6SkeletonPreview
import org.junit.Test

class EditNoteScreens : BasePaparazzi() {

    @Test
    fun createAccountScreenP6SkeletonPreview() {
        paparazzi.snapshot {
            CreateAccountScreenP6SkeletonPreview()
        }
    }

    @Test
    fun createOTPScreenP6SkeletonPreview() {
        paparazzi.snapshot {
            CreateOTPScreenP6SkeletonPreview()
        }
    }

    @Test
    fun createAccountScreenP6Preview() {
        paparazzi.snapshot {
            CreateAccountScreenP6Preview()
        }
    }

    @Test
    fun editAccountScreenP6Preview() {
        paparazzi.snapshot {
            EditAccountScreenP6Preview()
        }
    }

    @Test
    fun editAccountScreenSaveP6Preview() {
        paparazzi.snapshot {
            EditAccountScreenSaveP6Preview()
        }
    }

    @Test
    fun editOTPScreenP6SkeletonPreview() {
        paparazzi.snapshot {
            EditOTPScreenP6SkeletonPreview()
        }
    }

    @Test
    fun editOTPScreenP6Preview() {
        paparazzi.snapshot {
            EditOTPScreenP6Preview()
        }
    }

}