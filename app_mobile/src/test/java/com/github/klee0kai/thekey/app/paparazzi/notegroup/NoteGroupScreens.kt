package com.github.klee0kai.thekey.app.paparazzi.notegroup

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.notegroup.EditNoteGroupsSkeletonPreview
import com.github.klee0kai.thekey.app.ui.notegroup.components.EditGroupInfoContentInBoxPreview
import com.github.klee0kai.thekey.app.ui.notegroup.components.EditGroupInfoContentPreview
import org.junit.Test

class NoteGroupScreens : BasePaparazzi() {

    @Test
    fun editNoteGroupsSkeletonPreview() {
        paparazzi.snapshot {
            EditNoteGroupsSkeletonPreview()
        }
    }

    @Test
    fun editGroupInfoContentPreview() {
        paparazzi.snapshot {
            EditGroupInfoContentPreview()
        }
    }

    @Test
    fun editGroupInfoContentInBoxPreview() {
        paparazzi.snapshot {
            EditGroupInfoContentInBoxPreview()
        }
    }

}