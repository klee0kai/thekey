package com.github.klee0kai.thekey.app.paparazzi.setting.plugin

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginDummyScreenInstallErrorPreview
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginDummyScreenInstalledPreview
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginDummyScreenInstallingPreview
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginDummyScreenScreenPreview
import org.junit.Test

class PluginDummyScreens : BasePaparazzi() {

    @Test
    fun pluginDummyScreenScreenPreview() {
        paparazzi.snapshot {
            PluginDummyScreenScreenPreview()
        }
    }

    @Test
    fun pluginDummyScreenInstallingPreview() {
        paparazzi.snapshot {
            PluginDummyScreenInstallingPreview()
        }
    }

    @Test
    fun pluginDummyScreenInstalledPreview() {
        paparazzi.snapshot {
            PluginDummyScreenInstalledPreview()
        }
    }

    @Test
    fun pluginDummyScreenInstallErrorPreview() {
        paparazzi.snapshot {
            PluginDummyScreenInstallErrorPreview()
        }
    }


}