package com.github.klee0kai.thekey.app.ui.account

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarConst
import com.github.klee0kai.thekey.app.ui.designkit.components.AppBarStates
import com.github.klee0kai.thekey.app.ui.designkit.components.AppTitleImage

@Preview(showBackground = true)
@Composable
fun AccountScreen(

) {
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var siteInputText by remember { mutableStateOf("") }
    var loginInputText by remember { mutableStateOf("") }
    var passwordInputText by remember { mutableStateOf("") }
    var descriptionInputText by remember { mutableStateOf("") }


    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { backDispatcher?.onBackPressed() }) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                )
            }
        },
    ) { AppTitleImage() }

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
            visualTransformation = PasswordVisualTransformation(),
            value = siteInputText,
            onValueChange = { siteInputText = it },
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
            visualTransformation = PasswordVisualTransformation(),
            value = loginInputText,
            onValueChange = { loginInputText = it },
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
            visualTransformation = PasswordVisualTransformation(),
            value = passwordInputText,
            onValueChange = { passwordInputText = it },
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
            visualTransformation = PasswordVisualTransformation(),
            value = descriptionInputText,
            onValueChange = { descriptionInputText = it },
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

            }
        ) {
            Text(stringResource(R.string.save))
        }


    }


}