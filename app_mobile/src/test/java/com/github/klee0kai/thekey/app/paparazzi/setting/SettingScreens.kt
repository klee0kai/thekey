package com.github.klee0kai.thekey.app.paparazzi.setting

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.settings.SettingsScreenPreview
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.SectionHeaderPreview
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.StatusPreferencePreview
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.StatusPreferenceShortPreview
import com.github.klee0kai.thekey.core.ui.devkit.components.settings.SwitchPreferencePreview
import org.junit.Test

class SettingScreens : BasePaparazzi() {

    @Test
    fun settingsScreenPreview() {
        paparazzi.snapshot {
            SettingsScreenPreview()
        }
    }

    @Test
    fun settingSwitchItemPreview() {
        paparazzi.snapshot {
            SwitchPreferencePreview()
        }
    }

    @Test
    fun statusPreferencePreview() {
        paparazzi.snapshot {
            StatusPreferencePreview()
        }
    }

    @Test
    fun statusPreferenceShortPreview() {
        paparazzi.snapshot {
            StatusPreferenceShortPreview()
        }
    }

    @Test
    fun settingGroupItemPreview() {
        paparazzi.snapshot {
            SectionHeaderPreview()
        }
    }

}