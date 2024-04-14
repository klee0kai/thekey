package com.github.klee0kai.thekey.app.ui.scanqr

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Text
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.app.ui.scanqr.components.CameraQrScanner
import kotlinx.coroutines.launch

@SuppressLint("RestrictedApi")
@Composable
fun ScanQRCodeScreen() {
    val scope = rememberCoroutineScope()
    val router = LocalRouter.current
    val permissionHelper = remember { DI.permissionsHelper() }
    val permGranded = remember { permissionHelper.checkPermissions(permissionHelper.cameraPermissions()) }

    if (permGranded) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Green)
        ) {
            CameraQrScanner(
                onFound = { barcodes ->
                    barcodes.forEach {
                        scope.launch {
                            router.snack("qrcode ${it.rawValue}")
                        }
                    }
                }
            )
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize()
        )
        {
            Button(onClick = {
                scope.launch {
                    permissionHelper.askPermissionsIfNeed(permissionHelper.cameraPermissions(), TextProvider("Scan QrCode"))
                }
            }) {
                Text(text = "grand permission")
            }
        }
    }

}

@Preview
@Composable
private fun ScanQRCodeScreenPreview() = AppTheme {
    ScanQRCodeScreen()
}