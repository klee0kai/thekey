package com.github.klee0kai.thekey.app.ui.scanqr.components

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.CameraController.COORDINATE_SYSTEM_VIEW_REFERENCED
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun CameraQrScanner(
    modifier: Modifier = Modifier,
    onFound: (List<Barcode>) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val context = LocalView.current.context

    AndroidView(factory = {
        val previewView = PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            this.scaleType = PreviewView.ScaleType.FILL_CENTER
            this.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder()
                    .build()
                    .apply {
                        setSurfaceProvider(previewView.getSurfaceProvider())
                    }

                val barcodeScanner = BarcodeScanning
                    .getClient(
                        BarcodeScannerOptions.Builder()
                            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                            .build()
                    )

                val qrCodeAnalyser = ImageAnalysis.Builder()
                    .build()
                    .apply {
                        setAnalyzer(ContextCompat.getMainExecutor(context),
                            MlKitAnalyzer(
                                listOf(barcodeScanner),
                                COORDINATE_SYSTEM_VIEW_REFERENCED,
                                ContextCompat.getMainExecutor(context)
                            ) { result: MlKitAnalyzer.Result? ->
                                // The value of result.getResult(barcodeScanner) can be used directly for drawing UI overlay.

                            }
                        )
                    }
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(context as LifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, preview, qrCodeAnalyser)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }, ContextCompat.getMainExecutor(context))

        scope.launch {
            val scanner = BarcodeScanning.getClient(
                BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
                    .build()
            )

            while (isActive) {
                delay(40)
                previewView.bitmap?.let { bitmap ->
                    val image = InputImage.fromBitmap(bitmap, 0)
                    scanner.process(image).addOnCompleteListener {
                        if (it.result.isNotEmpty()) {
                            it.result.forEach { Timber.d("qrcode ${it}") }
                            onFound.invoke(it.result)
                        }
                    }
                }
            }
        }

        previewView
    })

}



