package com.github.klee0kai.thekey.app.ui.note

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarStates
import com.github.klee0kai.thekey.app.ui.navigation.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.model.NoteDestination
import com.github.klee0kai.thekey.app.ui.navigation.toIdentifier

@Preview(showBackground = true)
@Composable
fun NoteScreen(
    args: NoteDestination = NoteDestination(),
) {
    val navigator = LocalRouter.current
    val presenter = remember { DI.notePresenter(args.toIdentifier()) }
    val isEditNote = args.notePtr != 0L
    var isSkeleton by remember { mutableStateOf(isEditNote) }
    var note by remember { mutableStateOf(DecryptedNote()) }

    LaunchedEffect(Unit) {
        if (!isEditNote) return@LaunchedEffect
        isSkeleton = true
        note = presenter.note().await()
        isSkeleton = false
    }


    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { navigator.back() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
    ) { Text(text = stringResource(id = R.string.account)) }

    ConstraintLayout(
        modifier = Modifier
            .padding(
                top = 16.dp + AppBarConst.appBarSize,
                bottom = 16.dp,
                start = 16.dp,
                end = 16.dp
            )
            .fillMaxSize()
    ) {
        val (
            siteTextField, loginTextField,
            passwTextField, descriptionTextField,
            generatePasswButton, saveButton,
        ) = createRefs()

        OutlinedTextField(
            modifier = Modifier
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
            onValueChange = { note = note.copy(site = it) },
            label = { Text(stringResource(R.string.site)) }
        )

        OutlinedTextField(
            modifier = Modifier
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
            onValueChange = { note = note.copy(login = it) },
            label = { Text(stringResource(R.string.login)) }
        )


        OutlinedTextField(
            modifier = Modifier
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
            onValueChange = { note = note.copy(passw = it) },
            label = { Text(stringResource(R.string.password)) }
        )


        OutlinedTextField(
            modifier = Modifier
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
            onValueChange = { note = note.copy(desc = it) },
            label = { Text(stringResource(R.string.description)) }
        )

        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(generatePasswButton) {
                    bottom.linkTo(saveButton.top, margin = 12.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            onClick = {

            }
        ) {
            Text(stringResource(R.string.passw_generate))
        }

        FilledTonalButton(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(saveButton) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            onClick = {
                presenter.save(note)
            }
        ) {
            Text(stringResource(R.string.save))
        }


    }


}