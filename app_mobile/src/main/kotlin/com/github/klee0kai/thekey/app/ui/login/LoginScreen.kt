package com.github.klee0kai.thekey.app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.login.components.SelectedStorageElement
import com.github.klee0kai.thekey.app.ui.login.presenter.LoginPresenter
import com.github.klee0kai.thekey.app.ui.login.presenter.isLoginNotProcessingFlow
import com.github.klee0kai.thekey.app.ui.navigation.model.LoginDestination
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.Screen
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.ui.devkit.preview.PreviewDevices
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.collectAsStateFaded
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.linkToParent
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebouncedCons
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.visibleOnTargetAlpha
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.annotations.VisibleForTesting
import com.github.klee0kai.thekey.core.R as CoreR

@Composable
fun LoginScreen(
    dest: LoginDestination = LoginDestination(),
) = Screen {
    val scope = rememberCoroutineScope()
    val router by LocalRouter.currentRef
    val theme = LocalTheme.current
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    val presenter by rememberOnScreenRef { DI.loginPresenter(dest.identifier) }
    val currentStorageState by presenter!!.currentStorageFlow
        .collectAsState(key = Unit, initial = ColoredStorage())
    val isLoginNotProcessing by presenter!!.isLoginNotProcessingFlow
        .collectAsStateFaded(key = Unit, initial = true)

    var passwordInputText by remember { mutableStateOf(dest.prefilledPassw ?: "") }
    val imeVisible by animateTargetFaded(WindowInsets.isIme)

    ConstraintLayout(
        modifier = Modifier
            .padding(
                top = safeContentPaddings.calculateTopPadding(),
                bottom = safeContentPaddings.calculateBottomPadding() + 16.dp,
            )
            .padding(horizontal = safeContentPaddings.horizontal(minValue = 16.dp))
            .fillMaxSize()
    ) {
        val (
            logoIconField,
            offerBtnField,
            passwTextField,
            currentStorageField,
            storagesBtnField,
            loginBtnField,
        ) = createRefs()

        if (!imeVisible.current) {
            Image(
                painter = painterResource(id = CoreR.drawable.logo_big),
                contentDescription = stringResource(id = R.string.app_name),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .alpha(imeVisible.alpha)
                    .scale(0.4f)
                    .constrainAs(logoIconField) {
                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                        linkToParent(
                            bottom = passwTextField.top,
                        )
                    }
            )
        }

        AppTextField(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(passwTextField) {
                    linkToParent(verticalBias = 0.51f)
                },
            enabled = isLoginNotProcessing.current,
            visualTransformation = PasswordVisualTransformation(),
            value = passwordInputText,
            onValueChange = { passwordInputText = it },
            label = { Text(text = stringResource(R.string.master_passw)) },
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(
                onDone = rememberClickDebouncedCons { presenter?.login(passwordInputText, router) })
        )


        SelectedStorageElement(
            coloredStorage = currentStorageState,
            modifier = Modifier
                .constrainAs(currentStorageField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = passwTextField.start,
                        end = passwTextField.end,
                        bias = 0f,
                    )
                    top.linkTo(passwTextField.bottom, 16.dp)
                },
        )

        if (!imeVisible.current) {
            TextButton(
                modifier = Modifier
                    .alpha(imeVisible.alpha)
                    .constrainAs(offerBtnField) {
                        linkToParent(
                            top = passwTextField.bottom,
                        )
                    },
                colors = LocalColorScheme.current.grayTextButtonColors,
                onClick = rememberClickDebounced { }
            ) {
                Text(
                    text = stringResource(id = R.string.app_login_description),
                    textAlign = TextAlign.Center,
                    color = theme.colorScheme.textColors.primaryTextColor,
                )
            }
        }

        if (!imeVisible.current && dest.identifier.path.isBlank() || dest.forceAllowStorageSelect) {
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(imeVisible.alpha)
                    .constrainAs(storagesBtnField) {
                        bottom.linkTo(loginBtnField.top, margin = 12.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                colors = LocalColorScheme.current.grayTextButtonColors,
                onClick = rememberClickDebounced { presenter?.selectStorage(router) }
            ) {
                Text(
                    text = stringResource(R.string.storages),
                    style = theme.typeScheme.buttonText,
                )
            }
        }

        FilledTonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(loginBtnField) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            enabled = isLoginNotProcessing.current,
            onClick = rememberClickDebounced { presenter?.login(passwordInputText, router) }
        ) {
            Text(
                text = stringResource(R.string.login),
                style = theme.typeScheme.buttonText,
            )
        }
    }


    AppBarStates(
        navigationIcon = {
            IconButton(
                onClick = rememberClickDebounced { router?.showNavigationBoard() },
                content = { BackMenuIcon(isMenu = true) }
            )
        },
    )

}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun LoginScreenPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(
        object : PresentersModule {
            override fun loginPresenter(storageIdentifier: StorageIdentifier): LoginPresenter {
                return object : LoginPresenter {
                    override val currentStorageFlow = MutableStateFlow(
                        ColoredStorage(
                            path = "/appdata/some_path",
                            name = "editModeStorage"
                        )
                    )
                }
            }
        }
    )

    DebugDarkScreenPreview {
        LoginScreen()
    }
}


@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = PreviewDevices.PNOTE_LAND)
@Composable
fun LoginLangScreenPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(
        object : PresentersModule {
            override fun loginPresenter(storageIdentifier: StorageIdentifier): LoginPresenter {
                return object : LoginPresenter {
                    override val currentStorageFlow = MutableStateFlow(
                        ColoredStorage(
                            path = "/appdata/some_path",
                            name = "editModeStorage"
                        )
                    )
                }
            }
        }
    )
    DebugDarkScreenPreview {
        LoginScreen()
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun LoginScreenTabletPreview() {
    DI.hardResetToPreview()
    DI.initPresenterModule(
        object : PresentersModule {
            override fun loginPresenter(storageIdentifier: StorageIdentifier): LoginPresenter {
                return object : LoginPresenter {
                    override val currentStorageFlow = MutableStateFlow(
                        ColoredStorage(
                            path = "/appdata/some_path",
                        )
                    )
                }
            }
        }
    )

    DebugDarkScreenPreview {
        LoginScreen()
    }
}
