@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.editstorage

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.Screen
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.DeleteIconButton
import com.github.klee0kai.thekey.core.ui.devkit.components.dropdownfields.ColorGroupDropDownField
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.possitions.pxToDp
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.ifProduction
import com.github.klee0kai.thekey.core.utils.views.rememberClickArg
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetFaded
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun EditStorageScreen(
    path: String = "",
) = Screen {
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

    val isSaveAvailable by rememberTargetFaded { state.isSaveAvailable }
    val isRemoveAvailable by rememberTargetFaded { state.isRemoveAvailable }

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
                .ifProduction { animateContentSize() }
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
            onValueChange = rememberClickArg { presenter?.input { copy(name = it) } },
            label = { Text(stringResource(R.string.storage_name)) }
        )

        AppTextField(
            modifier = Modifier
                .ifProduction { animateContentSize() }
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
            onValueChange = rememberClickArg { presenter?.input { copy(desc = it) } },
            label = { Text(stringResource(R.string.storage_description)) }
        )

        ColorGroupDropDownField(
            modifier = Modifier
                .ifProduction { animateContentSize() }
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
                colors = theme.colorScheme.grayTextButtonColors,
                onClick = rememberClickDebounced { presenter?.changePassw(router) }
            ) {
                Text(
                    text = stringResource(R.string.change_password),
                    style = theme.typeScheme.buttonText,
                )
            }
        }

        if (isSaveAvailable.current) {
            FilledTonalButton(
                modifier = Modifier
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
        isVisible = scrollState.value == 0,
        navigationIcon = {
            IconButton(
                onClick = rememberClickDebounced { router?.back() },
                content = { BackMenuIcon() },
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
fun EditStorageScreenPreview() {
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

    DebugDarkScreenPreview {
        EditStorageScreen(path = "some/path/to/storage")
    }
}