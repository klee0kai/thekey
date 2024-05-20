package com.github.klee0kai.thekey.app.ui.genhist

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.domain.model.HistPassw
import com.github.klee0kai.thekey.app.ui.genhist.components.HistPasswItem
import com.github.klee0kai.thekey.app.ui.genhist.presenter.GenHistPresenter
import com.github.klee0kai.thekey.app.ui.navigation.model.GenHistDestination
import com.github.klee0kai.thekey.app.ui.navigation.storageIdentifier
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.views.collectAsState
import com.github.klee0kai.thekey.core.utils.views.rememberOnScreenRef
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import kotlinx.coroutines.flow.MutableStateFlow
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun GenHistScreen(
    dest: GenHistDestination = GenHistDestination(),
) {
    val router = LocalRouter.current
    val presenter by rememberOnScreenRef { DI.genHistPresenter(dest.storageIdentifier()) }
    val hist by presenter!!.histFlow.collectAsState(key = Unit, initial = null)

    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { router.back() }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        },
    ) { Text(text = stringResource(id = R.string.gen_history)) }

    ConstraintLayout {
        LazyColumn(
            modifier = Modifier
                .windowInsetsPadding(WindowInsets.safeContent)
                .padding(top = AppBarConst.appBarSize)
                .fillMaxSize()

        ) {
            hist?.forEach { passw ->
                item {
                    HistPasswItem(passw = passw)
                }
            }
        }

    }
}

@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun GenHistScreenPreview() = EdgeToEdgeTemplate {
    AppTheme {
        DI.initPresenterModule(object : PresentersModule {
            override fun genHistPresenter(storageIdentifier: StorageIdentifier) = object : GenHistPresenter {
                override val histFlow = MutableStateFlow(
                    listOf(
                        HistPassw(Dummy.dummyId, passw = "#@1", isLoaded = true),
                        HistPassw(Dummy.dummyId, passw = "dsa#$@1", isLoaded = true),
                        HistPassw(Dummy.dummyId, passw = "dsa#d!@", isLoaded = true),
                        HistPassw(Dummy.dummyId),
                        HistPassw(Dummy.dummyId),
                        HistPassw(Dummy.dummyId, passw = "d2451", isLoaded = true),
                    )
                )
            }
        })
        GenHistScreen()
    }
}