package com.github.klee0kai.thekey.app.paparazzi

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams
import org.junit.Rule

open class BasePaparazzi {

    @get:Rule
    val paparazzi = Paparazzi(
        environment = detectEnvironment().run {
            // https://github.com/cashapp/paparazzi/issues/1025
            copy(compileSdkVersion = 33, platformDir = platformDir.replace("34", "33"))
        },
        deviceConfig = DeviceConfig.PIXEL_6,
        renderingMode = SessionParams.RenderingMode.SHRINK,
    )

}