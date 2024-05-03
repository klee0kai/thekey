package com.github.klee0kai.thekey.app.ui.settings.plugin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.PluginIdentifier
import com.github.klee0kai.thekey.app.features.model.isInstalled
import com.github.klee0kai.thekey.app.ui.designkit.AppTheme
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.app.ui.navigation.model.PluginDestination
import com.github.klee0kai.thekey.app.utils.views.collectAsStateCrossFaded
import com.github.klee0kai.thekey.app.utils.views.rememberOnScreenRef

@Composable
fun PluginScreen(
    desc: PluginDestination = PluginDestination(),
) {
    val router = LocalRouter.current
    val presenter by rememberOnScreenRef { DI.pluginPresenter(PluginIdentifier(desc.feature)) }
    val feature by presenter!!.feature.collectAsStateCrossFaded(key = Unit, initial = null)

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = 16.dp + AppBarConst.appBarSize,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            ),
    ) {
        val (
            descField,
            installField,
            statusField,
        ) = createRefs()

        Text(
            modifier = Modifier
                .alpha(feature.alpha)
                .constrainAs(descField) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = parent.top,
                        bottom = parent.bottom,
                    )
                },
            text = stringResource(id = feature.current?.feature?.descRes ?: R.string.emty)
        )

        TextButton(
            modifier = Modifier.constrainAs(installField) {
                linkTo(
                    start = parent.start,
                    end = parent.end,
                    top = parent.top,
                    bottom = parent.bottom,
                    verticalBias = 1f,
                    horizontalBias = 1f,
                )
            },
            onClick = {
                when {
                    feature.current?.status?.isInstalled == true -> presenter?.uninstall()
                    else -> presenter?.install()
                }
            }
        ) {
            Text(
                modifier = Modifier
                    .alpha(feature.alpha),
                text = stringResource(
                    id = when {
                        feature.current?.status?.isInstalled == true -> R.string.uninstall
                        else -> R.string.install
                    }
                )
            )
        }

        Text(
            modifier = Modifier
                .alpha(feature.alpha)
                .constrainAs(statusField) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        top = parent.top,
                        bottom = parent.bottom,
                        verticalBias = 0.8f,
                    )
                },
            text = "status ${feature.current?.status}"
        )
    }

    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { router.back() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = null)
            }
        },
        titleContent = {
            Text(
                modifier = Modifier.alpha(feature.alpha),
                text = stringResource(id = feature.current?.feature?.titleRes ?: R.string.feature)
            )
        },
    )

}

@Preview
@Composable
private fun PluginScreenPreview() = AppTheme {
    PluginScreen()
}