package com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.scanqr

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.AndroidHelpersModule
import com.github.klee0kai.thekey.app.perm.PermissionsHelperDummy
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetFaded
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.navigation.cameraPermissions
import com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.scanqr.components.CameraPreviewCompose
import com.github.klee0kai.thekey.dynamic.qrcodescanner.ui.scanqr.components.qrCodeUserScanner
import com.github.klee0kai.thekey.dynamic.qrcodescanner.utils.rememberSuspendLambda
import com.github.klee0kai.thekey.feature.qrcodescanner.R
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting

sealed interface CameraState {
    data object NoState : CameraState
    data object Started : CameraState
    data object Error : CameraState
}

@Composable
fun ScanQRCodeScreen() {
    val scope = rememberCoroutineScope()
    val theme = LocalTheme.current
    val router by LocalRouter.currentRef
    val context = LocalContext.current
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val permissionHelper = remember { DI.permissionsHelper() }
    var permGranded by remember { mutableStateOf(permissionHelper.checkPermissions(permissionHelper.cameraPermissions())) }
    val permGrandedAnimated by animateTargetFaded(permGranded)
    var cameraState by remember { mutableStateOf<CameraState>(CameraState.NoState) }
    var screenClosed by remember { mutableStateOf(false) }
    var isQrCodeError by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (permGrandedAnimated.current) {
            CameraPreviewCompose(
                useCasesLambda = rememberSuspendLambda {
                    buildList {
                        val useCase = context.qrCodeUserScanner(
                            onFound = { barcodes ->
                                scope.launch {
                                    if (screenClosed) return@launch
                                    val barcode = barcodes.firstOrNull() ?: return@launch
                                    router?.backWithResult(barcode.rawValue)
                                    screenClosed = true
                                }
                            }
                        )
                        if (useCase != null) {
                            add(useCase)
                        } else {
                            isQrCodeError = true
                        }
                    }
                },
                onCameraStarted = { cameraState = CameraState.Started },
                onError = { cameraState = CameraState.Error },
            )
        }

        when {
            !permGrandedAnimated.current -> {
                TextButton(
                    modifier = Modifier
                        .padding(
                            horizontal = safeContentPaddings.horizontal(16.dp),
                            vertical = safeContentPaddings.calculateBottomPadding() + 16.dp
                        )
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    colors = theme.colorScheme.grayTextButtonColors,
                    onClick = {
                        scope.launch {
                            with(permissionHelper) {
                                router?.askPermissionsIfNeed(
                                    perms = permissionHelper.cameraPermissions(),
                                    purpose = TextProvider("qrCode")
                                )
                            }
                            permGranded = permissionHelper
                                .checkPermissions(permissionHelper.cameraPermissions())
                        }
                    }
                ) {
                    Text(
                        text = "grand permission",
                        style = theme.typeScheme.buttonText,
                    )
                }
            }

            isQrCodeError || cameraState == CameraState.Error -> {
                Text(
                    modifier = Modifier
                        .padding(
                            horizontal = safeContentPaddings.horizontal(16.dp),
                            vertical = safeContentPaddings.calculateBottomPadding() + 16.dp
                        )
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    text = stringResource(id = R.string.camera_error),
                    style = theme.typeScheme.header,
                )
            }

            cameraState != CameraState.Started -> {
                Text(
                    modifier = Modifier
                        .padding(
                            horizontal = safeContentPaddings.horizontal(16.dp),
                            vertical = safeContentPaddings.calculateBottomPadding() + 16.dp
                        )
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    text = stringResource(id = R.string.camera_starting),
                    style = theme.typeScheme.header,
                )
            }
        }
    }
}

@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview
@Composable
fun ScanQRCodeScreenNoPermissionPreview() {
    DI.hardResetToPreview()
    DI.initAndroidHelpersModule(object : AndroidHelpersModule {
        override fun permissionsHelper() = PermissionsHelperDummy(false)
    })

    DebugDarkScreenPreview {
        ScanQRCodeScreen()
    }
}

@VisibleForTesting
@OptIn(DebugOnly::class)
@Preview
@Composable
fun ScanQRCodeScreenPreview() {
    DI.hardResetToPreview()
    DI.initAndroidHelpersModule(object : AndroidHelpersModule {
        override fun permissionsHelper() = PermissionsHelperDummy(true)
    })
    DebugDarkScreenPreview {
        ScanQRCodeScreen()
    }
}