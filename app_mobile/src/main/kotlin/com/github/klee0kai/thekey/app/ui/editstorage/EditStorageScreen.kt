@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.editstorage

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.alpha
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
import com.github.klee0kai.thekey.app.utils.views.currentViewSizeState
import com.github.klee0kai.thekey.app.utils.views.keyboardAsState
import com.github.klee0kai.thekey.app.utils.views.onGlobalPositionState
import timber.log.Timber

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
    val viewSize by currentViewSizeState()
    val scrollState = rememberScrollState()

    val bottomSaveButton = viewSize.height > 500.dp
    val saveInToolbarAlpha by animateFloatAsState(
        targetValue = if (bottomSaveButton) 0f else 1f,
        label = "variants visible animate"
    )

    Timber.d("scroll state ${scrollState.value}")
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
        isVisible = scrollState.value == 0,
        navigationIcon = {
            IconButton(onClick = { navigator.back() }) {
                Icon(
                    Icons.Filled.ArrowBack,
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


    ConstraintLayout(
        optimizationLevel = 0,
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
                "121",
                "121",
                "121",
                "121",
                "121",
                "121",
                "121",
                "121",
                "121",
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


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 16.dp + AppBarConst.appBarSize,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
    ) {
        Spacer(modifier = Modifier.weight(1f))

        SnackbarHost(
            hostState = DI.snackbarHostState(),
            modifier = Modifier.fillMaxWidth()
        )

        if (bottomSaveButton) {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = {
                    presenter.save(storage)
                }
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }


}