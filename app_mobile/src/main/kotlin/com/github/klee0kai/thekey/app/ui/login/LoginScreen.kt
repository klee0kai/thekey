package com.github.klee0kai.thekey.app.ui.login

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.domain.model.ColoredStorage
import com.github.klee0kai.thekey.app.ui.login.presenter.LoginPresenter
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DoneIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.preview.PreviewDevices
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.minInsets
import com.github.klee0kai.thekey.core.utils.views.toAnnotationString
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import com.github.klee0kai.thekey.core.R as CoreR

@Composable
fun LoginScreen() {
    val scope = rememberCoroutineScope()
    val router = LocalRouter.current
    val presenter = remember { DI.loginPresenter() }
    val pathInputHelper = remember { DI.pathInputHelper() }
    val currentStorageState by presenter.currentStorageFlow.collectAsState(Unit, initial = ColoredStorage())
    var passwordInputText by remember { mutableStateOf("") }
    val imeVisible by animateTargetCrossFaded(WindowInsets.isIme)

    val shortStoragePath = with(pathInputHelper) {
        currentStorageState.path
            .shortPath()
            .toAnnotationString()
            .coloredPath()
    }

    BackHandler(enabled = router.isNavigationBoardIsOpen()) {
        when {
            router.isNavigationBoardIsOpen() -> scope.launch { router.hideNavigationBoard() }
        }
    }

    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { scope.launch { router.showNavigationBoard() } }) {
                Icon(Icons.Filled.Menu, contentDescription = null)
            }
        },
        actions = {
            if (imeVisible.current) {
                DoneIconButton(
                    modifier = Modifier.alpha(imeVisible.alpha),
                    onClick = { presenter.login(passwordInputText) }
                )
            }
        }

    )

    ConstraintLayout(
        modifier = Modifier
            .imePadding()
            .windowInsetsPadding(WindowInsets.safeContent.minInsets(16.dp))
            .fillMaxSize()
    ) {
        val (
            logoIcon, appDesc, passwTextField,
            storageName, storagePath,
            storagesButton, loginButton
        ) = createRefs()

        if (!imeVisible.current) {
            Image(
                painter = painterResource(id = CoreR.drawable.logo_big),
                contentDescription = stringResource(id = CoreR.string.app_name),
                modifier = Modifier
                    .alpha(imeVisible.alpha)
                    .constrainAs(logoIcon) {
                        linkTo(
                            start = parent.start,
                            top = parent.top,
                            end = parent.end,
                            bottom = appDesc.top,
                            verticalBias = 0.7f,
                        )
                    }
            )
        }

        Text(
            text = stringResource(id = R.string.app_login_description),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(appDesc) {
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = passwTextField.top,
                        verticalBias = 0.8f,
                    )
                }

        )

        AppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(passwTextField) {
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0.53f,
                    )
                },
            visualTransformation = PasswordVisualTransformation(),
            value = passwordInputText,
            onValueChange = { passwordInputText = it },
            label = { Text(stringResource(R.string.password)) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = {
                    presenter.login(passwordInputText)
                })
        )

        Text(
            text = currentStorageState.name,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .constrainAs(storageName) {
                    linkTo(
                        start = passwTextField.start,
                        end = passwTextField.end,
                        bias = 0f,
                    )
                    top.linkTo(passwTextField.bottom, 16.dp)
                }
        )

        Text(
            text = shortStoragePath,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier
                .constrainAs(storagePath) {
                    linkTo(
                        start = passwTextField.start,
                        end = passwTextField.end,
                        bias = 1f,
                    )
                    top.linkTo(storageName.bottom, 8.dp)
                }
        )

        if (!imeVisible.current) {
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(imeVisible.alpha)
                    .constrainAs(storagesButton) {
                        bottom.linkTo(loginButton.top, margin = 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                colors = LocalColorScheme.current.grayTextButtonColors,
                onClick = {
                    presenter.selectStorage()
                }
            ) {
                Text(stringResource(R.string.storages))
            }

            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(imeVisible.alpha)
                    .constrainAs(loginButton) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                onClick = { presenter.login(passwordInputText) }
            ) {
                Text(stringResource(R.string.login))
            }
        }
    }


}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun LoginScreenPreview() = EdgeToEdgeTemplate {
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(
            object : PresentersModule {
                override fun loginPresenter(): LoginPresenter {
                    return object : LoginPresenter {
                        override val currentStorageFlow = MutableStateFlow(ColoredStorage(path = "/app_folder/some_path", name = "editModeStorage"))
                    }
                }
            }
        )
        LoginScreen()
    }
}


@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = PreviewDevices.PNOTE_LAND)
@Composable
fun LoginLangScreenPreview() = EdgeToEdgeTemplate {
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(
            object : PresentersModule {
                override fun loginPresenter(): LoginPresenter {
                    return object : LoginPresenter {
                        override val currentStorageFlow = MutableStateFlow(ColoredStorage(path = "/app_folder/some_path", name = "editModeStorage"))
                    }
                }
            }
        )
        LoginScreen()
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun LoginScreenTabletPreview() = EdgeToEdgeTemplate {
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(
            object : PresentersModule {
                override fun loginPresenter(): LoginPresenter {
                    return object : LoginPresenter {
                        override val currentStorageFlow = MutableStateFlow(ColoredStorage(path = "/app_folder/some_path", name = "editModeStorage"))
                    }
                }
            }
        )
        LoginScreen()
    }
}
