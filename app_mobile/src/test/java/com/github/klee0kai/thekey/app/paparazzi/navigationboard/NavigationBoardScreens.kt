package com.github.klee0kai.thekey.app.paparazzi.navigationboard

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.navigationboard.StorageNavigationBoardEmptyPreview
import com.github.klee0kai.thekey.app.ui.navigationboard.StorageNavigationBoardNoCurrentPreview
import com.github.klee0kai.thekey.app.ui.navigationboard.StorageNavigationBoardPreview
import org.junit.Test

class NavigationBoardScreens : BasePaparazzi() {

    @Test
    fun storageNavigationBoardPreview() {
        paparazzi.snapshot {
            StorageNavigationBoardPreview()
        }
    }

    @Test
    fun storageNavigationBoardNoCurrentPreview() {
        paparazzi.snapshot {
            StorageNavigationBoardNoCurrentPreview()
        }
    }

    @Test
    fun storageNavigationBoardEmptyPreview() {
        paparazzi.snapshot {
            StorageNavigationBoardEmptyPreview()
        }
    }

}