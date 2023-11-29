package com.github.klee0kai.thekey.app.ui.designkit.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.R

@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleAppBar(
    title: String = stringResource(R.string.app_name),
    backClick: (() -> Unit)? = null
) {
    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary,
        ),

        title = { Text(title) },
        navigationIcon = {
            if (backClick != null) {
                IconButton(onClick = { backClick.invoke() }) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = null, modifier = Modifier)
                }
            }
        },
    )

}