package com.github.klee0kai.thekey.app.ui.scanqr.components

import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.LifecycleOwner
import com.github.klee0kai.thekey.app.utils.coroutine.await
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun CameraPreviewCompose(
    modifier: Modifier = Modifier,
    userCases: List<UseCase> = emptyList(),
    onError: (Exception) -> Unit = {},
) {
    val scope = rememberCoroutineScope()
    val context = LocalView.current.context

    AndroidView(
        modifier = modifier,
        factory = {
            val previewView = PreviewView(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                this.scaleType = PreviewView.ScaleType.FILL_CENTER
                this.implementationMode = PreviewView.ImplementationMode.PERFORMANCE
            }

            scope.launch {
                try {
                    val cameraProvider = ProcessCameraProvider.getInstance(context).await()
                    val preview = Preview.Builder()
                        .build()
                        .apply {
                            setSurfaceProvider(previewView.getSurfaceProvider())
                        }

                    cameraProvider.unbindAll()
                    val camera = cameraProvider.bindToLifecycle(
                        context as LifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        UseCaseGroup.Builder()
                            .addUseCase(preview)
                            .apply { userCases.forEach { addUseCase(it) } }
                            .build()
                    )

                } catch (e: Exception) {
                    Timber.e(e)
                    onError(e)
                }
            }

            previewView
        })
}



