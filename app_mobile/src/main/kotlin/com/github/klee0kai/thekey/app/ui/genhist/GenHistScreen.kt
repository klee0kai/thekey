package com.github.klee0kai.thekey.app.ui.genhist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarStates
import com.github.klee0kai.thekey.app.ui.genhist.model.LazyPassw
import com.github.klee0kai.thekey.app.ui.genhist.model.dummyLazyPassw
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.model.GenHistDestination
import com.github.klee0kai.thekey.app.ui.navigation.storageIdentifier
import com.github.klee0kai.thekey.app.utils.views.animateTargetAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.skeleton

@Preview
@Composable
fun GenHistScreen(
    dest: GenHistDestination = GenHistDestination(),
) {
    val router = LocalRouter.current
    val presenter = remember { DI.genHistPresenter(dest.storageIdentifier()) }
    val hist = remember { mutableStateOf(emptyList<LazyPassw>()) }

    LaunchedEffect(key1 = Unit) {
        hist.value = presenter.histNoteProviders().await()
    }

    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { router.back() }) {
                Icon(
                    Icons.Filled.ArrowBack,
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
        LazyColumn(
            contentPadding = PaddingValues(top = 8.dp, bottom = 8.dp)
        ) {
            hist.value.forEach { passw ->
                item {
                    LazyPasswItem(passw = passw)
                }
            }
        }
    }
}


@Preview
@Composable
fun LazyPasswItem(
    modifier: Modifier = Modifier,
    passw: LazyPassw = dummyLazyPassw(),
    offset: Int = 0,
) {
    val fullPassw = remember { mutableStateOf("") }
    val fullPasswAnimated = animateTargetAlphaAsState(target = fullPassw.value)

    LaunchedEffect(key1 = Unit) {
        fullPassw.value = passw.fullValue().passw
    }
    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        val (colorGroup, path, description) = createRefs()

        Text(
            text = fullPasswAnimated.value.target,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .alpha(fullPasswAnimated.value.alpha)
                .skeleton { fullPasswAnimated.value.target.isBlank() }
                .fillMaxWidth(0.5f)
                .padding(2.dp)
                .constrainAs(path) {
                    linkTo(
                        top = parent.top,
                        bottom = parent.bottom,
                        start = colorGroup.end,
                        end = parent.end,
                        topMargin = 6.dp,
                        bottomMargin = 6.dp,
                        startMargin = 16.dp,
                        endMargin = 16.dp,
                        horizontalBias = 0f,
                        verticalBias = 0f,
                    )
                }
        )
    }
}
