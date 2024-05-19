package com.github.klee0kai.thekey.feature.qrcodescanner.paparazzi

import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import app.cash.paparazzi.detectEnvironment
import com.android.ide.common.rendering.api.SessionParams
import com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.scanqr.ScanQRCodeScreenNoPermissionPreview
import com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.scanqr.ScanQRCodeScreenPreview
import org.junit.Rule
import org.junit.Test

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
    fun scanQRCodeScreenNoPermissionPreview() {
        paparazzi.snapshot {
            ScanQRCodeScreenNoPermissionPreview()
        }
    }

    @Test
    fun scanQRCodeScreenPreview() {
        paparazzi.snapshot {
            ScanQRCodeScreenPreview()
        }
    }

}