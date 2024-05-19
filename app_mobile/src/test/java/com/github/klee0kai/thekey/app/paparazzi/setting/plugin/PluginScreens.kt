package com.github.klee0kai.thekey.app.paparazzi.setting.plugin

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginBuyPreview
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginInstallErrorPreview
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginInstalledPreview
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginInstallingPreview
import com.github.klee0kai.thekey.app.ui.settings.plugin.PluginNotInstalledPreview
import org.junit.Test

class PluginScreens : BasePaparazzi() {

    @Test
    fun pluginNotInstalledPreview() {
        paparazzi.snapshot {
            PluginNotInstalledPreview()
        }
    }

    @Test
    fun pluginBuyPreview() {
        paparazzi.snapshot {
            PluginBuyPreview()
        }
    }

    @Test
    fun pluginInstallingPreview() {
        paparazzi.snapshot {
            PluginInstallingPreview()
        }
    }

    @Test
    fun pluginInstalledPreview() {
        paparazzi.snapshot {
            PluginInstalledPreview()
        }
    }

    @Test
    fun pluginInstallErrorPreview() {
        paparazzi.snapshot {
            PluginInstallErrorPreview()
        }
    }

}