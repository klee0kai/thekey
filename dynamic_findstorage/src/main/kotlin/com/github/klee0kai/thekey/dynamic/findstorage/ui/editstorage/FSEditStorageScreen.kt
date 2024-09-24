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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.ui.navigation.model.ChangeStoragePasswordDestination
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.Screen
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DeleteIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields.ColorGroupDropDownField
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.possitions.pxToDp
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberClickArg
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetCrossFaded
import com.github.klee0kai.thekey.dynamic.findstorage.R
import com.github.klee0kai.thekey.dynamic.findstorage.di.FSDI
import com.github.klee0kai.thekey.dynamic.findstorage.di.hardResetToPreview
import com.github.klee0kai.thekey.dynamic.findstorage.di.modules.FSPresentersModule
import com.github.klee0kai.thekey.dynamic.findstorage.domain.model.FileItem
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.components.PathTextField
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model.FSEditStorageState
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model.StoragePathLabelState
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.model.StoragePathProviderHintState
import com.github.klee0kai.thekey.dynamic.findstorage.ui.editstorage.presenter.FSEditStoragePresenter
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun FSEditStorageScreen(
    path: String = "",
) = Screen {
    val router by LocalRouter.currentRef
    val view = LocalView.current
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef {
        FSDI.fsEditStoragePresenter(StorageIdentifier(path)).apply { init() }
    }
    val pathInputHelper = remember { FSDI.pathInputHelper() }
    val state by presenter!!.state.collectAsState(
        key = Unit,
        initial = FSEditStorageState(isSkeleton = false)
    )
    val scrollState = rememberScrollState()
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()

    val imeIsVisibleAnimated by animateTargetCrossFaded(WindowInsets.isIme)
    val isSaveAvailable by rememberTargetCrossFaded { state.isSaveAvailable }
    val isRemoveAvailable by rememberTargetCrossFaded { state.isRemoveAvailable }


    BackHandler(enabled = state.storagePathFieldExpanded || state.colorGroupExpanded) {
        presenter?.input { copy(storagePathFieldExpanded = false, colorGroupExpanded = false) }
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
        ) = createRefs()


        PathTextField(
            modifier = Modifier
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
            value = state.path,
            isSkeleton = state.isSkeleton,
            variants = state.storagePathVariants,
            label = when (state.storagePathLabel) {
                is StoragePathLabelState.CreateStoragePath -> stringResource(R.string.storage_path_to_create)
                is StoragePathLabelState.MovingStoragePath -> stringResource(R.string.storage_path_to_move)
                is StoragePathLabelState.Simple -> stringResource(R.string.storage_path)
            },
            providerHint = when (val hint = state.storagePathProviderHint) {
                is StoragePathProviderHintState.Empty -> stringResource(id = R.string.not_found_files)
                is StoragePathProviderHintState.AvailableStorages -> stringResource(id = R.string.available_files)
                is StoragePathProviderHintState.CreateFolderFrom -> stringResource(
                    id = R.string.creating_folder_from,
                    hint.parentFolder.userPath,
                )
            },
            expanded = state.storagePathFieldExpanded,
            onExpandedChange = rememberClickArg { presenter?.input { copy(storagePathFieldExpanded = it) } },
            onValueChange = rememberClickArg { presenter?.input { copy(path = it) } }
        )

        AppTextField(
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
                        startMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                        endMargin = safeContentPaddings.horizontal(minValue = 16.dp),
                    )
                },
            isSkeleton = state.isSkeleton,
            value = state.name,
            onValueChange = { presenter?.input { copy(name = it) } },
            label = { Text(stringResource(R.string.storage_name)) }
        )


        AppTextField(
            modifier = Modifier
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
            isSkeleton = state.isSkeleton,
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
            FilledTonalButton(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth(),
                onClick = { presenter?.save(router) }
            ) {
                Text(stringResource(R.string.save))
            }
        }

        ColorGroupDropDownField(
            modifier = Modifier
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
            isSkeleton = state.isSkeleton,
            selectedIndex = state.colorGroupSelectedIndex,
            variants = state.colorGroupVariants,
            expanded = state.colorGroupExpanded,
            onExpandedChange = rememberClickArg { presenter?.input { copy(colorGroupExpanded = it) } },
            onSelected = rememberClickArg {
                presenter?.input {
                    copy(
                        colorGroupSelectedIndex = it,
                        colorGroupExpanded = false,
                    )
                }
            },
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

        if (state.isEditMode) {
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                colors = LocalColorScheme.current.grayTextButtonColors,
                onClick = rememberClickDebounced {
                    router?.navigate(ChangeStoragePasswordDestination(path))
                }
            ) {
                Text(stringResource(R.string.change_password))
            }
        }

        if (isSaveAvailable.current) {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(isSaveAvailable.alpha),
                onClick = rememberClickDebounced { presenter?.save(router) }
            ) { Text(stringResource(R.string.save)) }
        }
    }

    AppBarStates(
        isVisible = scrollState.value == 0,
        navigationIcon = {
            IconButton(
                onClick = rememberClickDebounced { router?.back() },
                content = { BackMenuIcon() }
            )
        },
        titleContent = {
            when {
                !state.isSkeleton && state.isEditMode -> Text(text = stringResource(R.string.edit_storage))
                !state.isSkeleton && !state.isEditMode -> Text(text = stringResource(R.string.create_storage))
            }
        },
        actions = {
            if (isRemoveAvailable.current) {
                DeleteIconButton(
                    modifier = Modifier
                        .alpha(isRemoveAvailable.alpha),
                    onClick = rememberClickDebounced { presenter?.remove(router) }
                )
            }
        }
    )

}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun FSEditStorageScreenPreview() = DebugDarkScreenPreview {
    FSDI.hardResetToPreview()
    FSDI.initFSPresentersModule(object : FSPresentersModule {

        override fun fsEditStoragePresenter(storageIdentifier: StorageIdentifier) =
            object : FSEditStoragePresenter {
                override val state = MutableStateFlow(
                    FSEditStorageState(
                        path = TextFieldValue("/appdata/work"),
                        isSkeleton = false,
                        isSaveAvailable = true,
                    )
                )
            }
    })
    FSEditStorageScreen(
        path = "some/path/to/storage",
    )
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun FSEditStorageScreenSelectPathPreview() = DebugDarkScreenPreview {
    FSDI.hardResetToPreview()
    FSDI.initFSPresentersModule(object : FSPresentersModule {
        override fun fsEditStoragePresenter(storageIdentifier: StorageIdentifier) =
            object : FSEditStoragePresenter {
                override val state = MutableStateFlow(
                    FSEditStorageState(
                        isSkeleton = false,
                        isEditMode = true,
                        isSaveAvailable = true,
                        storagePathVariants = listOf(
                            FileItem(
                                absPath = "/app/thekey/data",
                                userPath = "/appdata",
                                isFolder = true,
                            ),
                            FileItem(
                                absPath = "/storage/emulated/0",
                                userPath = "/phoneStorage",
                                isFolder = true,
                            ),
                        ),
                        storagePathFieldExpanded = false,
                        colorGroupExpanded = true,
                        colorGroupVariants = listOf(
                            ColorGroup(Dummy.dummyId, keyColor = KeyColor.ORANGE),
                            ColorGroup(Dummy.dummyId, keyColor = KeyColor.CORAL),
                        )
                    )
                )
            }
    })
    FSEditStorageScreen(
        path = "some/path/to/storage",
    )
}