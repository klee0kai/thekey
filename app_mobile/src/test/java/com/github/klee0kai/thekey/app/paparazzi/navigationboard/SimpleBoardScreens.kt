package com.github.klee0kai.thekey.app.paparazzi.navigationboard

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.simpleboard.NavigationBoardContainerPreview
import com.github.klee0kai.thekey.app.ui.simpleboard.NavigationBoardEmptyPreview
import com.github.klee0kai.thekey.app.ui.simpleboard.NavigationBoardNoCurrentPreview
import com.github.klee0kai.thekey.app.ui.simpleboard.NavigationBoardTabletPreview
import com.github.klee0kai.thekey.app.ui.simpleboard.SimpleBoardPreview
import org.junit.Test

class SimpleBoardScreens : BasePaparazzi() {

    @Test
    fun navigationBoardContainerPreview() {
        paparazzi.snapshot {
            NavigationBoardContainerPreview()
        }
    }

    @Test
    fun navigationBoardNoCurrentPreview() {
        paparazzi.snapshot {
            NavigationBoardNoCurrentPreview()
        }
    }

    @Test
    fun navigationBoardEmptyPreview() {
        paparazzi.snapshot {
            NavigationBoardEmptyPreview()
        }
    }

    @Test
    fun navigationBoardTabletPreview() {
        paparazzi.snapshot {
            NavigationBoardTabletPreview()
        }
    }

    @Test
    fun storageNavigationBoardPreview() {
        paparazzi.snapshot {
            SimpleBoardPreview()
        }
    }

}