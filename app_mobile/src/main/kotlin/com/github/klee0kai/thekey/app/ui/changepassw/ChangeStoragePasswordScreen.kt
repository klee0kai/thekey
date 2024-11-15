@file:OptIn(ExperimentalFoundationApi::class)

package com.github.klee0kai.thekey.app.ui.changepassw

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.changepassw.model.ChangePasswordStorageState
import com.github.klee0kai.thekey.app.ui.changepassw.model.ConfirmIsWrong
import com.github.klee0kai.thekey.app.ui.changepassw.model.PasswordNotChanged
import com.github.klee0kai.thekey.app.ui.changepassw.presenter.ChangeStoragePasswordPresenterDummy
import com.github.klee0kai.thekey.app.ui.storage.notes.ColoredNoteElement
import com.github.klee0kai.thekey.app.ui.storage.notes.ColoredOtpNoteElement
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.Screen
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.pxToDp
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.collectAsStateFaded
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.ifProduction
import com.github.klee0kai.thekey.core.utils.views.rememberClickArg
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetFaded
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun ChangeStoragePasswordScreen(
    path: String,
) = Screen {
    val router by LocalRouter.currentRef
    val view = LocalView.current
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef {
        DI.changeStoragePasswordPresenter(StorageIdentifier(path))
    }
    val state by presenter!!.state.collectAsState(
        key = Unit,
        initial = ChangePasswordStorageState()
    )
    val storageItems by presenter!!.sortedStorageItems.collectAsStateFaded(
        key = Unit,
        initial = null
    )
    val isSaveAvailable by rememberTargetFaded { state.isSaveAvailable }
    val scrollState = rememberLazyListState()
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()

    DisposableEffect(key1 = Unit) {
        onDispose { presenter?.clean() }
    }
    LazyColumn(
        state = scrollState,
        modifier = Modifier
            .imePadding()
            .fillMaxSize()
            .defaultMinSize(minHeight = view.height.pxToDp()),
    ) {
        item(key = "header") {
            Spacer(
                modifier = Modifier
                    .height(8.dp + AppBarConst.appBarSize + safeContentPaddings.calculateTopPadding())
                    .ifProduction { animateItemPlacement() },
            )

            AppTextField(
                modifier = Modifier
                    .padding(horizontal = safeContentPaddings.horizontal(minValue = 16.dp))
                    .fillMaxWidth()
                    .ifProduction { animateItemPlacement() },
                value = state.currentPassw,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = rememberClickArg { presenter?.input { copy(currentPassw = it) } },
                label = { Text(stringResource(R.string.current_password)) }
            )

            AppTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = safeContentPaddings.horizontal(minValue = 16.dp))
                    .fillMaxWidth()
                    .ifProduction { animateItemPlacement() },
                value = state.newPassw,
                visualTransformation = PasswordVisualTransformation(),
                onValueChange = rememberClickArg { presenter?.input { copy(newPassw = it) } },
                label = { Text(stringResource(R.string.new_password)) }
            )

            AppTextField(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .padding(horizontal = safeContentPaddings.horizontal(minValue = 16.dp))
                    .fillMaxWidth()
                    .ifProduction { animateItemPlacement() },
                value = state.newPasswConfirm,
                visualTransformation = PasswordVisualTransformation(),
                supportingText = {
                    when (state.error) {
                        ConfirmIsWrong -> {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.confirm_is_wrong),
                                color = theme.colorScheme.redColor,
                            )
                        }

                        PasswordNotChanged -> {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = stringResource(id = R.string.password_not_changed),
                                color = theme.colorScheme.redColor,
                            )
                        }

                        null -> Unit
                    }
                },
                onValueChange = rememberClickArg { presenter?.input { copy(newPasswConfirm = it) } },
                label = { Text(stringResource(R.string.confirm_password)) }
            )

            Spacer(
                modifier = Modifier
                    .height(16.dp)
                    .ifProduction { animateItemPlacement() }
            )
        }

        storageItems.current?.forEach { item ->
            item(key = item) {
                when {
                    item.note != null -> {
                        ColoredNoteElement(
                            modifier = Modifier
                                .fillMaxWidth()
                                .ifProduction { animateItemPlacement() },
                            note = item.note,
                        )
                    }

                    item.otp != null -> {
                        ColoredOtpNoteElement(
                            modifier = Modifier
                                .fillMaxWidth()
                                .ifProduction { animateItemPlacement() },
                            otp = item.otp,
                        )
                    }
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        if (isSaveAvailable.current) {
            FilledTonalButton(
                modifier = Modifier
                    .background(
                        Brush.linearGradient(
                            0f to Color.Transparent,
                            0.4f to theme.colorScheme.androidColorScheme.background,
                            1f to theme.colorScheme.androidColorScheme.background,
                            start = Offset.Zero,
                            end = Offset(0f, Float.POSITIVE_INFINITY)
                        )
                    )
                    .padding(
                        top = 56.dp,
                        bottom = safeContentPaddings.calculateBottomPadding() + 16.dp
                    )
                    .padding(horizontal = safeContentPaddings.horizontal(minValue = 16.dp))
                    .fillMaxWidth()
                    .alpha(isSaveAvailable.alpha),
                onClick = rememberClickDebounced { presenter?.save(router) }
            ) {
                Text(
                    text = stringResource(R.string.save),
                    style = theme.typeScheme.buttonText,
                )
            }
        }
    }

    AppBarStates(
        isVisible = !scrollState.canScrollBackward,
        navigationIcon = {
            IconButton(
                onClick = rememberClickDebounced { router?.back() },
                content = { BackMenuIcon() }
            )
        },
        titleContent = { Text(text = stringResource(R.string.change_password)) },
    )

}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun ChangeStoragePasswordScreenPreview() = DebugDarkScreenPreview {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {
        override fun changeStoragePasswordPresenter(storageIdentifier: StorageIdentifier) =
            object : ChangeStoragePasswordPresenterDummy(
                state = ChangePasswordStorageState(
                    currentPassw = "df",
                    newPassw = "",
                    isSaveAvailable = true,
                ),
                notesCount = 30,
            ) {

            }
    })
    ChangeStoragePasswordScreen(path = "some/path/to/storage")
}
