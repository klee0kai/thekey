package com.github.klee0kai.thekey.app.paparazzi.setting

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.settings.SettingsScreenPreview
import com.github.klee0kai.thekey.app.ui.settings.items.SettingGroupItemPreview
import com.github.klee0kai.thekey.app.ui.settings.items.SettingItemPreview
import com.github.klee0kai.thekey.app.ui.settings.items.SettingSwitchItemPreview
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
            SettingItemPreview()
        }
    }

    @Test
    fun settingSwitchItemPreview() {
        paparazzi.snapshot {
            SettingSwitchItemPreview()
        }
    }

    @Test
    fun settingGroupItemPreview() {
        paparazzi.snapshot {
            SettingGroupItemPreview()
        }
    }

}