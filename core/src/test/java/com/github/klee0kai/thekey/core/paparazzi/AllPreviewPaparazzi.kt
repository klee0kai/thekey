package com.github.klee0kai.thekey.core.paparazzi

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams
import com.github.klee0kai.thekey.core.ui.gen.preview.allPreviews
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.annotations.IgnorePaparazzi
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test

@OptIn(DebugOnly::class)
class AllPreviewPaparazzi {

    @get:Rule
    val paparazzi = Paparazzi(
        environment = detectEnvironment().run {
            // https://github.com/cashapp/paparazzi/issues/1025
            copy(compileSdkVersion = 33, platformDir = platformDir.replace("34", "33"))
        },
        deviceConfig = DeviceConfig.PIXEL_6,
        renderingMode = SessionParams.RenderingMode.SHRINK,
    )

    @Test
    fun allScreensPaparazzi() = runBlocking {
        allPreviews()
            .filter { IgnorePaparazzi::class !in it.annotations }
            .forEach { preview ->
                println("screenshot ${preview.pkg} ${preview.methodName} ")

                paparazzi.snapshot(
                    preview.methodName,
                ) {
                    preview.content()
                }
            }
    }

}