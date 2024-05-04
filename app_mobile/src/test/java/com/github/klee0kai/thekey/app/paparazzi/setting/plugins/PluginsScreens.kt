package com.github.klee0kai.thekey.app.paparazzi.setting.plugins

import com.github.klee0kai.thekey.app.paparazzi.BasePaparazzi
import com.github.klee0kai.thekey.app.ui.settings.plugins.PluginsScreenPreview
import org.junit.Test

class PluginsScreens : BasePaparazzi() {

    @Test
    fun pluginsScreenPreview() {
        paparazzi.snapshot {
            PluginsScreenPreview()
        }
    }


}