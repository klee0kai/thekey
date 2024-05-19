package com.github.klee0kai.thekey.app.paparazzi.setting.plugin

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginApplyingScreenPreview
import org.junit.Test

class PluginApplyingScreens : BasePaparazzi() {

    @Test
    fun pluginApplyingScreenPreview() {
        paparazzi.snapshot {
            PluginApplyingScreenPreview()
        }
    }

}