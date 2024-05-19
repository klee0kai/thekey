package com.github.klee0kai.thekey.app.paparazzi.setting

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.designkit.settings.PreferencePreview
import com.github.klee0kai.thekey.app.ui.designkit.settings.SectionHeaderPreview
import com.github.klee0kai.thekey.app.ui.designkit.settings.SwitchPreferencePreview
import com.github.klee0kai.thekey.app.ui.settings.SettingsScreenPreview
import org.junit.Test

class SettingScreens : BasePaparazzi() {

    @Test
    fun settingsScreenPreview() {
        paparazzi.snapshot {
            SettingsScreenPreview()
        }
    }

    @Test
    fun settingItemPreview() {
        paparazzi.snapshot {
            PreferencePreview()
        }
    }

    @Test
    fun settingSwitchItemPreview() {
        paparazzi.snapshot {
            SwitchPreferencePreview()
        }
    }

    @Test
    fun settingGroupItemPreview() {
        paparazzi.snapshot {
            SectionHeaderPreview()
        }
    }

}