package com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.navigation

import androidx.compose.runtime.Composable
import com.github.klee0kai.thekey.app.ui.navigation.model.QRCodeScanDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.ui.navigation.screenresolver.ScreenResolver
import com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.scanqr.ScanQRCodeScreen

class ScreenResolverQRExt(
    private val origin: ScreenResolver,
) : ScreenResolver by origin {

    @Composable
    override fun screenOf(destination: Destination) {
        when (destination) {
            QRCodeScanDestination -> ScanQRCodeScreen()
            else -> origin.screenOf(destination = destination)
        }
    }

}