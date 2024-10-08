package com.github.klee0kai.thekey.feature.qrcodescanner.paparazzi

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.annotations.IgnorePaparazzi
import com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.scanqr.ScanQRCodeScreenPreview
import com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.scanqr.gen.preview.allPreviews
import org.junit.Rule
import org.junit.Test

@OptIn(DebugOnly::class)
class QRCodeScannerScreens {

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
    fun allScreenPreviews() {
        allPreviews()
            .filter { IgnorePaparazzi::class !in it.annotations }
            .forEach { preview ->
            println("screenshot ${preview.pkg} ${preview.methodName} ")

            paparazzi.snapshot(
                preview.methodName
            ) {
                preview.content()
            }
        }

    }

}