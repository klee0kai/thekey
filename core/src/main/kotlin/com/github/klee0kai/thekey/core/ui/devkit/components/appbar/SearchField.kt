package com.github.klee0kai.thekey.core.ui.devkit.components.appbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.components.text.AppTextField
import com.github.klee0kai.thekey.core.ui.devkit.theme.DefaultThemes
import com.github.klee0kai.thekey.core.utils.views.transparentColors
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun SearchField(
    textModifier: Modifier = Modifier,
    searchText: String,
    onSearch: (String) -> Unit = {},
    onClose: () -> Unit = {},
) {

    Box {
        AppTextField(
            modifier = textModifier
                .wrapContentHeight()
                .fillMaxWidth(),
            placeholder = {
                Text(
                    modifier = Modifier.alpha(0.4f),
                    text = stringResource(id = R.string.search),
                )
            },
            value = searchText,
            onValueChange = { onSearch(it) },
            colors = TextFieldDefaults.transparentColors(),
        )

        IconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd),
            onClick = { onClose.invoke() },
            content = { Icon(Icons.Filled.Close, contentDescription = null) }
        )
    }
}

@VisibleForTesting
@Preview
@Composable
fun SearchFieldEmptyPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    ) {
        SearchField(
            textModifier = Modifier,
            searchText = "",
        )
    }
}

@VisibleForTesting
@Preview
@Composable
fun SearchFieldTextPreview() = AppTheme(theme = DefaultThemes.darkTheme) {
    AppBarStates(
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(
                    Icons.AutoMirrored.Default.ArrowBack,
                    contentDescription = null,
                )
            }
        }
    ) {
        SearchField(
            textModifier = Modifier,
            searchText = "some text",
        )
    }
}