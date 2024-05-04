@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.editstorage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardReset
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.engine.model.Storage
import com.github.klee0kai.thekey.app.helpers.path.appendTKeyFormat
import com.github.klee0kai.thekey.app.helpers.path.removeTKeyFormat
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.app.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.app.utils.coroutine.awaitSec
import com.github.klee0kai.thekey.app.utils.views.AutoFillList
import com.github.klee0kai.thekey.app.utils.views.Keyboard
import com.github.klee0kai.thekey.app.utils.views.ViewPositionPx
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.currentViewSizeState
import com.github.klee0kai.thekey.app.utils.views.keyboardAsState
import com.github.klee0kai.thekey.app.utils.views.onGlobalPositionState
import com.github.klee0kai.thekey.app.utils.views.rememberOnScreen
import com.github.klee0kai.thekey.app.utils.views.toTextFieldValue
import com.github.klee0kai.thekey.app.utils.views.toTransformationText
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun EditStorageScreen(
    path: String = "",
) {
    val navigator = rememberOnScreen { DI.router() }
    val presenter = rememberOnScreen { DI.editStoragePresenter(StorageIdentifier(path)) }
    val pathInputHelper = rememberOnScreen { DI.pathInputHelper() }
    val userShortPathHelper = rememberOnScreen { DI.userShortPaths() }
    var storage by remember { mutableStateOf(Storage()) }

    var storagePathTextValue by remember { mutableStateOf(TextFieldValue()) }
    var storagePathFieldFocused by remember { mutableStateOf<Boolean>(false) }
    var storagePathVariants by remember { mutableStateOf<List<String>>(emptyList()) }
    var contentViewSize by remember { mutableStateOf<ViewPositionPx?>(null) }
    val keyboardState by keyboardAsState()
    val viewSize by currentViewSizeState()
    val scrollState = rememberScrollState()

    val bottomSaveButton = viewSize.height > 500.dp
    val saveInToolbarAlpha by animateAlphaAsState(!bottomSaveButton)

    LaunchedEffect(Unit) {
        storage = presenter.storageInfo.awaitSec() ?: return@LaunchedEffect
        storagePathTextValue = userShortPathHelper.shortPathName(storage.path)
            .removeTKeyFormat()
            .toTextFieldValue()
    }
    LaunchedEffect(keyboardState) {
        if (keyboardState == Keyboard.Closed) {
            storagePathFieldFocused = false
        }
    }
    LaunchedEffect(storagePathTextValue.text) {
        with(pathInputHelper) {
            storage = storage.copy(
                path = storagePathTextValue.text
                    .absolutePath()
                    ?.appendTKeyFormat()
                    ?: ""
            )
            storagePathTextValue.text
                .pathVariables()
                .collect {
                    storagePathVariants = it
                }
        }
    }
    BackHandler(enabled = storagePathFieldFocused) {
        storagePathFieldFocused = false
    }

    ConstraintLayout(optimizationLevel = 0,
        modifier = Modifier
            .verticalScroll(scrollState)
            .onGlobalPositionState { contentViewSize = it }
            .pointerInput(Unit) { detectTapGestures { storagePathFieldFocused = false } }
            .fillMaxSize()
            .padding(
                top = 16.dp + AppBarConst.appBarSize,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        val (
            pathTextField,
            nameTextField,
            descTextField,
            autofillList,
        ) = createRefs()

        OutlinedTextField(
            modifier = Modifier
                .onFocusChanged { storagePathFieldFocused = it.isFocused }
                .constrainAs(pathTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            visualTransformation = { input ->
                with(pathInputHelper) {
                    input.coloredPath()
                        .toTransformationText()
                }
            },
            value = storagePathTextValue,
            onValueChange = {
                with(pathInputHelper) {
                    storagePathFieldFocused = true
                    storagePathTextValue = it
                        .pathInputMask()
                }
            },
            label = { Text(stringResource(R.string.storage_path)) }
        )

        OutlinedTextField(
            modifier = Modifier
                .constrainAs(nameTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = pathTextField.bottom,
                        start = parent.start,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            value = storage.name,
            onValueChange = { storage = storage.copy(name = it) },
            label = { Text(stringResource(R.string.storage_name)) }
        )


        OutlinedTextField(
            modifier = Modifier
                .constrainAs(descTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = nameTextField.bottom,
                        start = parent.start,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            value = storage.description,
            onValueChange = { storage = storage.copy(description = it) },
            label = { Text(stringResource(R.string.storage_description)) }
        )


        AutoFillList(
            modifier = Modifier.constrainAs(autofillList) {
                height = Dimension.wrapContent
                width = Dimension.fillToConstraints
                linkTo(
                    start = pathTextField.start,
                    end = pathTextField.end,
                    top = pathTextField.bottom,
                    bottom = parent.bottom,
                    verticalBias = 0f,
                    bottomMargin = 16.dp
                )
            },
            isVisible = storagePathFieldFocused,
            variants = storagePathVariants,
            onSelected = { selected ->
                with(pathInputHelper) {
                    if (selected == null) return@AutoFillList
                    storagePathTextValue = storagePathTextValue.text
                        .folderSelected(selected)
                        .toTextFieldValue()
                }
            }
        )
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 16.dp + AppBarConst.appBarSize,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
    ) {
        if (bottomSaveButton) {
            FilledTonalButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                onClick = { presenter.save(storage) }
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }


    AppBarStates(
        isVisible = scrollState.value == 0,
        navigationIcon = {
            IconButton(onClick = {
                navigator.back()
            }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            if (saveInToolbarAlpha > 0) {
                IconButton(
                    modifier = Modifier.alpha(saveInToolbarAlpha),
                    onClick = { presenter.save(storage) }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = stringResource(id = R.string.save),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    ) { Text(text = stringResource(id = presenter.titleRes)) }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(showSystemUi = true, device = Devices.PIXEL_6)
@Composable
fun EditStorageScreenPreview() = AppTheme {
    DI.hardReset()
    EditStorageScreen(path = "some/path/to/storage")
}