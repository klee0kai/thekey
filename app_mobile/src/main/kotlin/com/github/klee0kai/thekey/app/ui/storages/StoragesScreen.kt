@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.app.ui.storages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.hardResetToPreview
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.EditStorageGroupDestination
import com.github.klee0kai.thekey.app.ui.storages.components.GroupsSelectContent
import com.github.klee0kai.thekey.app.ui.storages.components.InstallExternalSearchPromo
import com.github.klee0kai.thekey.app.ui.storages.components.StoragesListContent
import com.github.klee0kai.thekey.app.ui.storages.presenter.StoragesPresenterDummy
import com.github.klee0kai.thekey.core.domain.ColorGroup
import com.github.klee0kai.thekey.core.domain.externalStorages
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalColorScheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.SimpleBottomSheetScaffold
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.topContentAlphaFromDrag
import com.github.klee0kai.thekey.core.ui.devkit.bottomsheet.topContentOffsetFromDrag
import com.github.klee0kai.thekey.core.ui.devkit.components.FabSimpleInContainer
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppTitleImage
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.accumulate
import com.github.klee0kai.thekey.core.utils.views.animateTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.isIme
import com.github.klee0kai.thekey.core.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import com.github.klee0kai.thekey.core.utils.views.rememberTargetCrossFaded
import com.github.klee0kai.thekey.core.utils.views.visibleOnTargetAlpha
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import org.jetbrains.annotations.VisibleForTesting
import com.github.klee0kai.thekey.core.R as CoreR

private const val MainTitleId = 0
private const val SecondTittleId = 1

@Composable
fun StoragesScreen() {
    val router = LocalRouter.current
    val theme = LocalTheme.current
    val presenter by rememberOnScreenRef { DI.storagesPresenter().apply { init() } }

    val selectedGroup by presenter!!.selectedGroupId.collectAsState(key = Unit, initial = null)
    val groups by presenter!!.filteredColorGroups.collectAsState(key = Unit, initial = emptyList())
    val isExtStorageSelected by rememberTargetCrossFaded { selectedGroup == ColorGroup.externalStorages().id }

    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()
    var dragProgress by remember { mutableFloatStateOf(1f) }
    val mainTitleVisibility by accumulate<Boolean?>(null) { old ->
        when {
            dragProgress < 0.1 -> false
            dragProgress > 0.3 -> true
            else -> old
        }
    }
    val targetTitleId = rememberDerivedStateOf {
        when (mainTitleVisibility) {
            true -> MainTitleId
            false -> SecondTittleId
            null -> 0
        }
    }
    val showStoragesTitle by rememberTargetCrossFaded { dragProgress > 0.1f }
    val imeIsVisibleAnimated by animateTargetCrossFaded(WindowInsets.isIme)

    SimpleBottomSheetScaffold(
        topMargin = AppBarConst.appBarSize + safeContentPaddings.calculateTopPadding(),
        topContentSize = 170.dp,
        onDrag = { dragProgress = it },
        topContent = {
            GroupsSelectContent(
                modifier = Modifier
                    .alpha(dragProgress.topContentAlphaFromDrag())
                    .offset(y = dragProgress.topContentOffsetFromDrag()),
                colorGroups = groups,
                selectedGroup = selectedGroup,
                onAdd = { router.navigate(EditStorageGroupDestination()) },
                onGroupEdit = { router.navigate(EditStorageGroupDestination(it.id)) },
                onGroupSelected = { presenter?.selectGroup(it.id) },
            )
        },
        sheetContent = {
            if (!isExtStorageSelected.current) {
                StoragesListContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(isExtStorageSelected.alpha),
                    onEdit = { presenter?.editStorage(storagePath = it.path, router) },
                    onExport = { presenter?.exportStorage(storagePath = it.path, router) },
                    header = {
                        Text(
                            text = stringResource(id = CoreR.string.storages),
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .padding(start = 16.dp, top = 4.dp, bottom = 22.dp)
                                .alpha(showStoragesTitle.visibleOnTargetAlpha(true))
                        )
                    },
                    footer = {
                        Spacer(modifier = Modifier.height(safeContentPaddings.calculateBottomPadding()))
                    },
                )
            } else {
                InstallExternalSearchPromo()
            }
        }
    )

    if (isExtStorageSelected.current) {
        Column(
            modifier = Modifier
                .alpha(isExtStorageSelected.alpha)
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.safeContent)
                .padding(
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
        ) {
            Spacer(modifier = Modifier.weight(1f))
            if (!imeIsVisibleAnimated.current) {
                TextButton(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = LocalColorScheme.current.grayTextButtonColors,
                    onClick = { presenter?.importStorage(router) }
                ) {
                    val textRes = CoreR.string.import_storage
                    Text(stringResource(textRes))
                }
            }

            if (!imeIsVisibleAnimated.current) {
                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(imeIsVisibleAnimated.alpha),
                    onClick = { presenter?.installAutoSearchPlugin() }
                ) {
                    Text(stringResource(CoreR.string.install))
                }
            }
        }
    }

    AppBarStates(
        titleId = targetTitleId,
        navigationIcon = {
            IconButton(onClick = { router.back() }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        titleContent = { titleId ->
            when (titleId) {
                MainTitleId -> AppTitleImage()
                SecondTittleId -> Text(text = stringResource(id = CoreR.string.storages))
            }
        },
    )

    if (!isExtStorageSelected.current) {
        FabSimpleInContainer(
            modifier = Modifier.alpha(isExtStorageSelected.alpha),
            onClick = { router.navigate(EditStorageDestination()) },
            content = { Icon(Icons.Default.Add, contentDescription = "Add") }
        )
    }
}

@OptIn(DebugOnly::class)
@VisibleForTesting
@Composable
@Preview(device = Devices.PHONE)
fun StoragesScreenPreview() = EdgeToEdgeTemplate {
    AppTheme {
        DI.hardResetToPreview()
        DI.initPresenterModule(object : PresentersModule {
            override fun storagesPresenter() = object : StoragesPresenterDummy(
                groupsCount = 3,
                storagesCount = 3,
            ) {

            }
        })

        StoragesScreen()
    }
}

