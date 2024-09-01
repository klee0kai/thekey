@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.editstorage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.editstorage.model.EditStorageState
import com.github.klee0kai.thekey.app.ui.editstorage.presenter.EditStoragePresenter
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DeleteIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DoneIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.buttons.GroupCircle
import com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields.ColorGroupSelectPopupMenu
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.overlay.PopupMenu
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.onGlobalPositionState
import com.github.klee0kai.thekey.core.utils.possitions.pxToDp
import com.github.klee0kai.thekey.core.utils.possitions.rememberViewPosition
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.animateSkeletonModifier
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetCrossFaded
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterIsInstance
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun EditStorageScreen(
    path: String = "",
) {
    val router by LocalRouter.currentRef
    val view = LocalView.current
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef {
        DI.editStoragePresenter(StorageIdentifier(path)).apply { init() }
    }

    val state by presenter!!.state.collectAsState(
        key = Unit,
        initial = EditStorageState(isSkeleton = false)
    )
    val scrollState = rememberScrollState()
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()

    val imeIsVisibleAnimated by animateTargetCrossFaded(WindowInsets.isIme)
    val isSaveAvailable by rememberTargetCrossFaded { state.isSaveAvailable }
    val isRemoveAvailable by rememberTargetCrossFaded { state.isRemoveAvailable }
    val skeletonModifier by animateSkeletonModifier { state.isSkeleton }
    val groupSelectPosition = rememberViewPosition()
    val groupInteractionSource = remember { MutableInteractionSource() }

    LaunchedEffect(Unit) {
        groupInteractionSource.interactions.filterIsInstance<PressInteraction.Press>().collect {
            presenter?.input { copy(colorGroupExpanded = !colorGroupExpanded) }
        }
    }
    BackHandler(enabled = state.colorGroupExpanded) {
        presenter?.input { copy(colorGroupExpanded = false) }
    }
    ConstraintLayout(
        modifier = Modifier
            .imePadding()
            .verticalScroll(scrollState)
            .fillMaxSize()
            .defaultMinSize(minHeight = view.height.pxToDp()),
    ) {
        val (
            nameTextField,
            descTextField,
            colorGroupField,
        ) = createRefs()

        AppTextField(
            modifier = Modifier
                .constrainAs(nameTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = parent.top,
                        start = parent.start,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp + AppBarConst.appBarSize + safeContentPaddings.calculateTopPadding(),
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
            if (!imeIsVisibleAnimated.current) {
                FilledTonalButton(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .alpha(imeIsVisibleAnimated.alpha),
                    onClick = { presenter?.save(router) }
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }

        AppTextField(
            modifier = Modifier
                .onGlobalPositionState(groupSelectPosition)
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
            interactionSource = groupInteractionSource,
            readOnly = true,
            singleLine = true,
            value = when {
                state.colorGroupSelectedIndex < 0 -> stringResource(id = R.string.no_group)
                else -> state.colorGroupVariants
                    .getOrNull(state.colorGroupSelectedIndex)
                    ?.name
                    ?.takeIf { it.isNotBlank() }
                    ?: stringResource(id = R.string.no_name)
            },
            leadingIcon = {
                GroupCircle(
                    modifier = Modifier
                        .padding(4.dp),
                    colorScheme = theme.colorScheme.surfaceSchemas.surfaceScheme(
                        state.colorGroupVariants.getOrNull(state.colorGroupSelectedIndex)?.keyColor
                            ?: KeyColor.NOCOLOR
                    ),
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = state.colorGroupExpanded) },
            onValueChange = { },
            label = { Text(stringResource(R.string.group)) }
        )


        PopupMenu(
            visible = state.colorGroupExpanded,
            positionAnchor = groupSelectPosition,
            onDismissRequest = { presenter?.input { copy(colorGroupExpanded = false) } }
        ) {
            ColorGroupSelectPopupMenu(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 10.dp)
                    .fillMaxWidth(),
                variants = state.colorGroupVariants,
                onSelected = { _, idx ->
                    presenter?.input {
                        copy(
                            colorGroupSelectedIndex = idx,
                            colorGroupExpanded = false,
                        )
                    }
                }
            )
        }
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
                onClick = { presenter?.changePassw(router) }
            ) {
                Text(stringResource(R.string.change_password))
            }
        }

        if (!imeIsVisibleAnimated.current && isSaveAvailable.current) {
            FilledTonalButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(imeIsVisibleAnimated.alpha)
                    .alpha(isSaveAvailable.alpha),
                onClick = { presenter?.save(router) }
            ) { Text(stringResource(R.string.save)) }
        }
    }

    AppBarStates(
        isVisible = scrollState.value == 0,
        navigationIcon = {
            IconButton(onClick = { router?.back() }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
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
                    onClick = { presenter?.remove(router) }
                )
            }

            if (imeIsVisibleAnimated.current && isSaveAvailable.current) {
                DoneIconButton(
                    modifier = Modifier.alpha(imeIsVisibleAnimated.alpha),
                    onClick = { presenter?.save(router) }
                )
            }
        }
    )

}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun EditStorageScreenPreview() = DebugDarkScreenPreview {
    DI.hardResetToPreview()
    DI.initPresenterModule(object : PresentersModule {

        override fun editStoragePresenter(storageIdentifier: StorageIdentifier?) =
            object : EditStoragePresenter {
                override val state = MutableStateFlow(
                    EditStorageState(
                        isSkeleton = false,
                        isSaveAvailable = true,
                        isEditMode = true,
                    )
                )
            }
    })
    EditStorageScreen(path = "some/path/to/storage")
}