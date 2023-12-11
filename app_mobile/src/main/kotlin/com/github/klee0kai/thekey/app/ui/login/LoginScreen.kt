package com.github.klee0kai.thekey.app.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.github.klee0kai.thekey.app.R
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.navigation.Destination
import dev.olshevski.navigation.reimagined.navigate

@Preview(showBackground = true)
@Composable
fun LoginScreen() {
    val scope = rememberCoroutineScope()
    val presenter = remember { DI.loginPresenter() }
    val navigator = remember { DI.navigator() }

    var passwordInputText by remember { mutableStateOf("") }


    Scaffold(
        content = { padding ->
            ConstraintLayout(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
            ) {
                val (
                    logoIcon, appDesc, passwTextField,
                    storageName, storagePath,
                    storagesButton, loginButton
                ) = createRefs()

                Image(
                    painter = painterResource(id = R.drawable.logo_big),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier
                        .constrainAs(logoIcon) {
                            linkTo(
                                start = parent.start,
                                top = parent.top,
                                end = parent.end,
                                bottom = appDesc.top,
                                verticalBias = 0.7f,
                            )
                        }
                )

                Text(
                    text = stringResource(id = R.string.app_login_description),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(appDesc) {
                            linkTo(
                                start = parent.start,
                                top = parent.top,
                                end = parent.end,
                                bottom = passwTextField.top,
                                verticalBias = 0.8f,
                            )
                        }

                )

                OutlinedTextField(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .constrainAs(passwTextField) {
                            linkTo(
                                start = parent.start,
                                top = parent.top,
                                end = parent.end,
                                bottom = parent.bottom,
                                verticalBias = 0.53f,
                            )
                        },
                    visualTransformation = PasswordVisualTransformation(),
                    value = passwordInputText,
                    onValueChange = { passwordInputText = it },
                    label = { Text(stringResource(R.string.password)) }
                )

                TextButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(storagesButton) {
                            bottom.linkTo(loginButton.top, margin = 12.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    onClick = {
                        navigator.navigate(Destination.StoragesScreen)
                    }
                ) {
                    Text(stringResource(R.string.storages))
                }

                FilledTonalButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(loginButton) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    onClick = {
                        presenter.login(passwordInputText)
                    }
                ) {
                    Text(stringResource(R.string.login))
                }
            }
        }
    )
}

