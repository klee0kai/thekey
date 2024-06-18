package com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DeleteIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DoneIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields.ColorGroupDropDownField
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.AutoFillList
import com.github.klee0kai.thekey.core.utils.views.Keyboard
import com.github.klee0kai.thekey.core.utils.views.animateSkeletonModifier
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.keyboardAsState
import com.github.klee0kai.thekey.core.utils.views.pxToDp
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.toTextFieldValue
import com.github.klee0kai.thekey.core.utils.views.toTransformationText
import com.github.klee0kai.thekey.dynamic.findstorage.R
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.di.hardResetToPreview
import com.github.klee0kai.thekey.dynamic.findstorage.di.modules.FSPresentersModule
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model.FSEditStorageState
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.presenter.FSEditStoragePresenter
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun FSEditStorageScreen(
    path: String = "",
) {
    val router = LocalRouter.current
    val view = LocalView.current
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef { FSDI.fsEditStoragePresenter(StorageIdentifier(path)).apply { init() } }
    val pathInputHelper = remember { FSDI.pathInputHelper() }
    val state by presenter!!.state.collectAsState(key = Unit, initial = FSEditStorageState(isSkeleton = false))
    val keyboardState by keyboardAsState()
    val scrollState = rememberScrollState()
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()

    val imeIsVisibleAnimated by animateTargetCrossFaded(WindowInsets.isIme)
    val isSaveAvailable by rememberTargetCrossFaded { state.isSaveAvailable }
    val isRemoveAvailable by rememberTargetCrossFaded { state.isRemoveAvailable }
    val skeletonModifier by animateSkeletonModifier { state.isSkeleton }

    LaunchedEffect(keyboardState) {
        if (keyboardState == Keyboard.Closed) {
            presenter?.input { copy(storagePathFieldFocused = false) }
        }
    }
    BackHandler(enabled = state.storagePathFieldFocused) {
        presenter?.input { copy(storagePathFieldFocused = false) }
    }
    ConstraintLayout(
        modifier = Modifier
            .imePadding()
            .verticalScroll(scrollState)
            .fillMaxSize()
            .defaultMinSize(minHeight = view.height.pxToDp()),
    ) {
        val (
            pathTextField,
            nameTextField,
            descTextField,
            colorGroupField,
            autofillList,
        ) = createRefs()


        AppTextField(
            modifier = Modifier
                .onFocusChanged { presenter?.input { copy(storagePathFieldFocused = true) } }
                .constrainAs(pathTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp + AppBarConst.appBarSize + safeContentPaddings.calculateTopPadding(),
                        startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                        endMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                    )
                },
            visualTransformation = { input ->
                with(pathInputHelper) {
                    input.coloredPath()
                        .toTransformationText()
                }
            },
            value = state.pathNoExt,
            onValueChange = {
                with(pathInputHelper) {
                    presenter?.input {
                        copy(
                            storagePathFieldFocused = true,
                            pathNoExt = it.pathInputMask(),
                        )
                    }
                }
            },
            label = { Text(stringResource(R.string.storage_path)) }
        )


        AppTextField(
            modifier = Modifier
                .then(skeletonModifier)
                .constrainAs(nameTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = pathTextField.bottom,
                        start = parent.start,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                        startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                        endMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                    )
                },
            value = state.name,
            onValueChange = { presenter?.input { copy(name = it) } },
            label = { Text(stringResource(R.string.storage_name)) }
        )


        AppTextField(
            modifier = Modifier
                .then(skeletonModifier)
                .constrainAs(descTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = nameTextField.bottom,
                        start = nameTextField.start,
                        end = nameTextField.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            value = state.desc,
            onValueChange = { presenter?.input { copy(desc = it) } },
            label = { Text(stringResource(R.string.storage_description)) }
        )



        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeContent)
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp),
        ) {
            if (!imeIsVisibleAnimated.current) {
                FilledTonalButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .alpha(imeIsVisibleAnimated.alpha),
                    onClick = { presenter?.save() }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }

        AutoFillList(
            modifier = Modifier
                .constrainAs(autofillList) {
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
            isVisible = state.storagePathFieldFocused,
            variants = state.storagePathVariants,
            onSelected = { selected ->
                with(pathInputHelper) {
                    if (selected == null) return@AutoFillList
                    presenter?.input {
                        copy(
                            pathNoExt = path.folderSelected(selected)
                                .toTextFieldValue()
                        )
                    }
                }
            }
        )

        ColorGroupDropDownField(
            modifier = Modifier
                .then(skeletonModifier)
                .fillMaxWidth(0.5f)
                .constrainAs(colorGroupField) {
                    linkTo(
                        top = descTextField.bottom,
                        start = descTextField.start,
                        end = descTextField.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        horizontalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            selectedIndex = state.colorGroupSelected,
            variants = state.colorGroupVariants,
            expanded = state.colorGroupExpanded,
            onExpandedChange = { presenter?.input { copy(colorGroupExpanded = it) } },
            onSelected = { presenter?.input { copy(colorGroupSelected = it, colorGroupExpanded = false) } },
            label = { Text(stringResource(R.string.group)) }
        )
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = safeContentPaddings.calculateBottomPadding() + 16.dp)
            .padding(horizontal = safeContentPaddings.horizontal(minValue = 16.dp)),
    ) {
        Spacer(modifier = Modifier.weight(1f))
        if (!imeIsVisibleAnimated.current && isSaveAvailable.current) {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(imeIsVisibleAnimated.alpha)
                    .alpha(isSaveAvailable.alpha),
                onClick = { presenter?.save() }
            ) { Text(stringResource(R.string.save)) }
        }
    }


    AppBarStates(
        isVisible = scrollState.value == 0,
        navigationIcon = {
            IconButton(onClick = { router.back() }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        titleContent = { Text(text = stringResource(id = if (state.isEditMode) R.string.edit_storage else R.string.create_storage)) },
        actions = {
            if (isRemoveAvailable.current) {
                DeleteIconButton(
                    modifier = Modifier
                        .alpha(isRemoveAvailable.alpha),
                    onClick = { presenter?.remove() }
                )
            }

            if (imeIsVisibleAnimated.current && isSaveAvailable.current) {
                DoneIconButton(
                    modifier = Modifier.alpha(imeIsVisibleAnimated.alpha),
                    onClick = { presenter?.save() }
                )
            }
        }
    )

}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun FSEditStorageScreenPreview() = EdgeToEdgeTemplate {
    FSDI.hardResetToPreview()
    FSDI.initFSPresentersModule(object : FSPresentersModule {

        override fun fsEditStoragePresenter(storageIdentifier: StorageIdentifier) = object : FSEditStoragePresenter {
            override val state = MutableStateFlow(
                FSEditStorageState(
                    isSkeleton = false,
                    isSaveAvailable = true,
                )
            )
        }
    })
    AppTheme(theme = DefaultThemes.darkTheme) {
        FSEditStorageScreen(
            path = "some/path/to/storage",
        )
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun FSEditStorageScreenSelectPathPreview() = EdgeToEdgeTemplate {
    FSDI.hardResetToPreview()
    FSDI.initFSPresentersModule(object : FSPresentersModule {

        override fun fsEditStoragePresenter(storageIdentifier: StorageIdentifier) = object : FSEditStoragePresenter {
            override val state = MutableStateFlow(
                FSEditStorageState(
                    isSkeleton = false,
                    isSaveAvailable = true,
                    storagePathVariants = listOf("/appdata", "/phoneData"),
                    storagePathFieldFocused = true,
                    colorGroupExpanded = true,
                    colorGroupVariants = listOf(
                        ColorGroup(Dummy.dummyId, keyColor = KeyColor.ORANGE),
                        ColorGroup(Dummy.dummyId, keyColor = KeyColor.CORAL),
                    )
                )
            )
        }
    })
    AppTheme(theme = DefaultThemes.darkTheme) {
        FSEditStorageScreen(
            path = "some/path/to/storage",
        )
    }
}