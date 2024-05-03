package com.github.klee0kai.thekey.app.ui.genhist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.di.modules.PresentersModule
import com.github.klee0kai.thekey.app.domain.model.HistPassw
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.app.ui.genhist.components.HistPasswItem
import com.github.klee0kai.thekey.app.ui.genhist.presenter.GenHistPresenter
import com.github.klee0kai.thekey.app.ui.navigation.model.GenHistDestination
import com.github.klee0kai.thekey.app.ui.navigation.storageIdentifier
import com.github.klee0kai.thekey.app.utils.common.Dummy
import com.github.klee0kai.thekey.app.utils.views.collectAsState
import com.github.klee0kai.thekey.app.utils.views.rememberOnScreenRef
import kotlinx.coroutines.flow.MutableStateFlow

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

    ConstraintLayout(
        modifier = Modifier
            .padding(top = AppBarConst.appBarSize)
            .fillMaxSize()
    ) {

        LazyColumn {
            hist?.forEach { passw ->
                item {
                    HistPasswItem(passw = passw)
                }
            }
        }

    }
}

@Preview(
    showSystemUi = true,
    device = Devices.PIXEL_6,
)
@Composable
private fun GenHistScreenPreview() = AppTheme {
    DI.initPresenterModule(object : PresentersModule {
        override fun genHistPresenter(storageIdentifier: StorageIdentifier) = object : GenHistPresenter {
            override val histFlow = MutableStateFlow(
                listOf(
                    HistPassw(Dummy.dummyId, passw = "#@1"),
                    HistPassw(Dummy.dummyId, passw = "dsa#$@1"),
                    HistPassw(Dummy.dummyId, passw = "dsa#d!@"),
                    HistPassw(Dummy.dummyId),
                    HistPassw(Dummy.dummyId),
                    HistPassw(Dummy.dummyId, passw = "d2451"),
                )
            )
        }
    })
    GenHistScreen()
}