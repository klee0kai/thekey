package com.github.klee0kai.thekey.app.paparazzi.designkit

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarTitlePreview1
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarTitlePreview2
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.SearchFieldEmptyPreview
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.SearchFieldTextPreview
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.SecondaryTabsPreview
import org.junit.Test

class AppBarScreens : BasePaparazzi() {

    @Test
    fun appBarTitlePreview1() {
        paparazzi.snapshot {
            AppBarTitlePreview1()
        }
    }


    @Test
    fun appBarTitlePreview2() {
        paparazzi.snapshot {
            AppBarTitlePreview2()
        }
    }

    @Test
    fun searchFieldEmptyPreview() {
        paparazzi.snapshot {
            SearchFieldEmptyPreview()
        }
    }

    @Test
    fun searchFieldTextPreview() {
        paparazzi.snapshot {
            SearchFieldTextPreview()
        }
    }

    @Test
    fun secondaryTabsPreview() {
        paparazzi.snapshot {
            SecondaryTabsPreview()
        }
    }


}