package com.github.klee0kai.thekey.app.paparazzi.storage

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.storage.StorageScreenAccountsPreview
import com.github.klee0kai.thekey.app.ui.storage.StorageScreenAccountsSearchPreview
import com.github.klee0kai.thekey.app.ui.storage.StorageScreenGeneratePreview
import com.github.klee0kai.thekey.app.ui.storage.genpassw.GenPasswordContentPreview
import com.github.klee0kai.thekey.app.ui.storage.notes.ColoredNoteDummy
import com.github.klee0kai.thekey.app.ui.storage.notes.ColoredNoteDummyNoGroup
import com.github.klee0kai.thekey.app.ui.storage.notes.ColoredNoteSkeleton
import com.github.klee0kai.thekey.app.ui.storage.notes.ColoredOtpNoteDummyNoGroupPreview
import com.github.klee0kai.thekey.app.ui.storage.notes.ColoredOtpNoteDummyPreview
import com.github.klee0kai.thekey.app.ui.storage.notes.ColoredOtpNoteSkeletonPreview
import com.github.klee0kai.thekey.app.ui.storage.notes.NoteDropDownMenuNoPreview
import com.github.klee0kai.thekey.app.ui.storage.notes.NoteDropDownMenuWithGroupsPreview
import com.github.klee0kai.thekey.app.ui.storage.notes.NotesContentPreview
import com.github.klee0kai.thekey.app.ui.storage.notes.NotesListContentPreview
import com.github.klee0kai.thekey.app.ui.storage.notes.NotesListContentTitlePreview
import org.junit.Test

class StorageScreens : BasePaparazzi() {

    @Test
    fun storageScreenAccountsPreview() {
        paparazzi.snapshot {
            StorageScreenAccountsPreview()
        }
    }

    @Test
    fun storageScreenAccountsSearchPreview() {
        paparazzi.snapshot {
            StorageScreenAccountsSearchPreview()
        }
    }

    @Test
    fun storageScreenGeneratePreview() {
        paparazzi.snapshot {
            StorageScreenGeneratePreview()
        }
    }

    @Test
    fun coloredNoteSkeleton() {
        paparazzi.snapshot {
            ColoredNoteSkeleton()
        }
    }

    @Test
    fun coloredNoteDummy() {
        paparazzi.snapshot {
            ColoredNoteDummy()
        }
    }

    @Test
    fun coloredNoteDummyNoGroup() {
        paparazzi.snapshot {
            ColoredNoteDummyNoGroup()
        }
    }

    @Test
    fun coloredOtpNoteSkeletonPreview() {
        paparazzi.snapshot {
            ColoredOtpNoteSkeletonPreview()
        }
    }

    @Test
    fun coloredOtpNoteDummyPreview() {
        paparazzi.snapshot {
            ColoredOtpNoteDummyPreview()
        }
    }

    @Test
    fun coloredOtpNoteDummyNoGroupPreview() {
        paparazzi.snapshot {
            ColoredOtpNoteDummyNoGroupPreview()
        }
    }

    @Test
    fun noteDropDownMenuWithGroupsPreview() {
        paparazzi.snapshot {
            NoteDropDownMenuWithGroupsPreview()
        }
    }

    @Test
    fun noteDropDownMenuNoPreview() {
        paparazzi.snapshot {
            NoteDropDownMenuNoPreview()
        }
    }

    @Test
    fun notesContentPreview() {
        paparazzi.snapshot {
            NotesContentPreview()
        }
    }

    @Test
    fun notesListContentPreview() {
        paparazzi.snapshot {
            NotesListContentPreview()
        }
    }

    @Test
    fun notesListContentTitlePreview() {
        paparazzi.snapshot {
            NotesListContentTitlePreview()
        }
    }

    @Test
    fun genPasswordContentPreview() {
        paparazzi.snapshot {
            GenPasswordContentPreview()
        }
    }


}