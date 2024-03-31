package com.github.klee0kai.thekey.app.ui.note

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarStates
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.identifier
import com.github.klee0kai.thekey.app.ui.navigation.model.NoteDestination
import com.github.klee0kai.thekey.app.utils.views.animateAlphaAsState
import com.github.klee0kai.thekey.app.utils.views.collectAsState
import com.github.klee0kai.thekey.app.utils.views.collectAsStateCrossFaded
import com.github.klee0kai.thekey.app.utils.views.currentViewSizeState
import com.github.klee0kai.thekey.app.utils.views.rememberDerivedStateOf
import com.github.klee0kai.thekey.app.utils.views.skeleton

@Preview(showBackground = true)
@Composable
fun NoteScreen(
    args: NoteDestination = NoteDestination(),
) {
    val navigator = LocalRouter.current
    val presenter = remember {
        DI.notePresenter(args.identifier()).apply {
            init(args.prefilled)
        }
    }
    val isEditNote = args.notePtr != 0L
    val note by presenter.note.collectAsState(key = Unit)
    val originNote = presenter.originNote.collectAsStateCrossFaded()
    val isSkeleton = rememberDerivedStateOf {
        isEditNote && originNote.value.current == null
    }
    val alpha = rememberDerivedStateOf { if (isEditNote) originNote.value.alpha else 1f }
    val scrollState = rememberScrollState()
    val viewSize by currentViewSizeState()
    val bottomButtons = rememberDerivedStateOf { viewSize.height > 700.dp }
    val saveInToolbarAlpha = animateAlphaAsState(!bottomButtons.value)

    ConstraintLayout(
        optimizationLevel = 0,
        modifier = Modifier
            .verticalScroll(scrollState)
            .fillMaxSize()
            .padding(
                top = 16.dp + AppBarConst.appBarSize,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
    ) {
        val (
            siteTextField, loginTextField,
            passwTextField, descriptionTextField,
        ) = createRefs()

        OutlinedTextField(
            modifier = Modifier
                .alpha(alpha.value)
                .skeleton(isSkeleton.value)
                .constrainAs(siteTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        start = parent.start,
                        top = parent.top,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            value = note.site,
            onValueChange = { presenter.note.value = note.copy(site = it) },
            label = { Text(stringResource(R.string.site)) }
        )

        OutlinedTextField(
            modifier = Modifier
                .alpha(alpha.value)
                .skeleton(isSkeleton.value)
                .constrainAs(loginTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = siteTextField.bottom,
                        start = parent.start,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            value = note.login,
            onValueChange = { presenter.note.value = note.copy(login = it) },
            label = { Text(stringResource(R.string.login)) }
        )


        OutlinedTextField(
            modifier = Modifier
                .alpha(alpha.value)
                .skeleton(isSkeleton.value)
                .constrainAs(passwTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = loginTextField.bottom,
                        start = parent.start,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            value = note.passw,
            onValueChange = { presenter.note.value = note.copy(passw = it) },
            label = { Text(stringResource(R.string.password)) }
        )


        OutlinedTextField(
            modifier = Modifier
                .alpha(alpha.value)
                .skeleton(isSkeleton.value)
                .constrainAs(descriptionTextField) {
                    width = Dimension.fillToConstraints
                    linkTo(
                        top = passwTextField.bottom,
                        start = parent.start,
                        end = parent.end,
                        bottom = parent.bottom,
                        verticalBias = 0f,
                        topMargin = 8.dp,
                    )
                },
            value = note.desc,
            onValueChange = { presenter.note.value = note.copy(desc = it) },
            label = { Text(stringResource(R.string.description)) }
        )

    }

    if (bottomButtons.value) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 16.dp + AppBarConst.appBarSize,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                ),
        ) {
            Spacer(modifier = Modifier.weight(1f))

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { presenter.generate() }
            ) {
                Text(stringResource(R.string.passw_generate))
            }

            FilledTonalButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = { presenter.save() }
            ) {
                Text(stringResource(R.string.save))
            }

        }
    }


    AppBarStates(
        isVisible = scrollState.value == 0,
        navigationIcon = {
            IconButton(onClick = { navigator.back() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
        titleContent = { Text(text = stringResource(id = R.string.account)) },
        actions = {
            if (saveInToolbarAlpha.value > 0) {
                IconButton(
                    modifier = Modifier.alpha(saveInToolbarAlpha.value),
                    onClick = {
                        presenter.save()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Done,
                        contentDescription = stringResource(id = R.string.save),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    )
}