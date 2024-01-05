@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.editstorage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.model.Storage
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarStates
import com.github.klee0kai.thekey.app.ui.navigation.back
import com.github.klee0kai.thekey.app.utils.coroutine.await
import com.github.klee0kai.thekey.app.utils.views.AutoFillList
import com.github.klee0kai.thekey.app.utils.views.Keyboard
import com.github.klee0kai.thekey.app.utils.views.ViewPositionPx
import com.github.klee0kai.thekey.app.utils.views.keyboardAsState
import com.github.klee0kai.thekey.app.utils.views.onGlobalPositionState
import com.github.klee0kai.thekey.app.utils.views.toDp

@Preview
@Composable
fun EditStorageScreen(
    path: String? = null,
) {
    val navigator = remember { DI.navigator() }
    val presenter = remember { DI.editStoragePresenter(StorageIdentifier(path)) }
    var storage by remember { mutableStateOf(Storage()) }

    var pathTextValue by remember { mutableStateOf(TextFieldValue()) }
    var storagePathFieldFocused by remember { mutableStateOf<Boolean>(false) }
    var contentViewSize by remember { mutableStateOf<ViewPositionPx?>(null) }
    val keyboardState by keyboardAsState()

    val bottomSaveButton = (contentViewSize?.toDp()?.size?.height ?: 0.dp) > 500.dp


    LaunchedEffect(Unit) {
        storage = presenter.storageInfo.await(300L) ?: return@LaunchedEffect
    }
    LaunchedEffect(keyboardState) {
        if (keyboardState == Keyboard.Closed) {
            storagePathFieldFocused = false
        }
    }
    BackHandler(enabled = storagePathFieldFocused) {
        storagePathFieldFocused = false
    }

    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { navigator.back() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        actions = {
            if (!bottomSaveButton) {
                IconButton(onClick = { presenter.save(storage) }) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = stringResource(id = R.string.save),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    ) { Text(text = stringResource(id = presenter.titleRes)) }

    ConstraintLayout(
        modifier = Modifier
            .onGlobalPositionState { contentViewSize = it }
            .pointerInput(Unit) {
                detectTapGestures { storagePathFieldFocused = false }
            }
            .padding(
                top = 16.dp + AppBarConst.appBarSize,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
            .fillMaxSize()
    ) {
        val (
            pathTextField,
            nameTextField,
            descTextField,
            saveButton,
            snackOverview,
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
            value = pathTextValue,
            onValueChange = {
                storagePathFieldFocused = true
                pathTextValue = it
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

        if (bottomSaveButton) {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(saveButton) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                onClick = {
                    presenter.save(storage)
                }
            ) {
                Text(stringResource(R.string.save))
            }
        }


        SnackbarHost(
            hostState = DI.snackbarHostState(),
            modifier = Modifier.constrainAs(snackOverview) {
                height = Dimension.wrapContent
                width = Dimension.fillToConstraints
                linkTo(
                    top = parent.top,
                    start = parent.start,
                    end = parent.end,
                    bottom = saveButton.top,
                    verticalBias = 1f,
                )
            }
        )


        AutoFillList(
            modifier = Modifier.constrainAs(autofillList) {
                height = Dimension.preferredWrapContent
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
            variants = listOf(
                "start",
                "end",
            ),
            onSelected = { selected ->
                if (selected == null) {
                    storagePathFieldFocused = false
                } else {
                    pathTextValue = TextFieldValue(
                        text = selected,
                        selection = TextRange(selected.length)
                    )
                }
            }
        )
    }


}