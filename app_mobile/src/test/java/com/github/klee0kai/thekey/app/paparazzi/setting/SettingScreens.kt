package com.github.klee0kai.thekey.app.paparazzi.setting

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.settings.SettingsScreenPreview
import org.junit.Test

class SettingScreens : BasePaparazzi() {

    @Test
    fun settingsScreenPreview() {
        paparazzi.snapshot {
            SettingsScreenPreview()
        }
    }


}