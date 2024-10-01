package com.github.klee0kai.thekey.core.ui.commercial

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.stone.type.wrappers.getValue
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.devkit.LocalTheme
import com.github.klee0kai.thekey.core.ui.devkit.Screen
import com.github.klee0kai.thekey.core.ui.devkit.components.appbar.AppBarStates
import com.github.klee0kai.thekey.core.ui.devkit.icons.BackMenuIcon
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.core.utils.views.DebugDarkScreenPreview
import com.github.klee0kai.thekey.core.utils.views.currentRef
import com.github.klee0kai.thekey.core.utils.views.horizontal
import com.github.klee0kai.thekey.core.utils.views.linkToParent
import com.github.klee0kai.thekey.core.utils.views.rememberClickDebounced

@Composable
fun InstallCommercialScreen() = Screen {
    val scope = rememberCoroutineScope()
    val router by LocalRouter.currentRef
    val theme = LocalTheme.current
    val safeContentPaddings = WindowInsets.safeContent.asPaddingValues()

    ConstraintLayout(
        modifier = Modifier
            .padding(
                top = safeContentPaddings.calculateTopPadding(),
                bottom = safeContentPaddings.calculateBottomPadding() + 16.dp,
            )
            .padding(horizontal = safeContentPaddings.horizontal(minValue = 16.dp))
            .fillMaxSize()
    ) {
        val (
            messageField,
        ) = createRefs()

        Text(
            text = stringResource(id = R.string.install_commercial_version),
            modifier = Modifier.constrainAs(messageField) {
                linkToParent()
            },
            textAlign = TextAlign.Center,
        )

    }


    AppBarStates(
        titleContent = {
            Text(text = stringResource(id = R.string.feature_not_available))
        },
        navigationIcon = {
            IconButton(
                onClick = rememberClickDebounced { router?.back() },
                content = { BackMenuIcon() }
            )
        },
    )

}


@OptIn(DebugOnly::class)
@Composable
@Preview
fun InstallCommercialScreenPreview() = DebugDarkScreenPreview {
    InstallCommercialScreen()
}