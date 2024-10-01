@file:OptIn(ExperimentalMaterial3Api::class)

package com.github.klee0kai.thekey.core.ui.commercial

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.github.klee0kai.thekey.core.R
import com.github.klee0kai.thekey.core.ui.devkit.dialogs.SimpleDialog
import com.github.klee0kai.thekey.core.ui.navigation.model.SimpleDialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.TextProvider

@Composable
fun InstallCommercialVersionDialog() {
    SimpleDialog(
        dest = SimpleDialogDestination(
            title = TextProvider(R.string.feature_not_available),
            message = TextProvider(R.string.install_commercial_version),
        )
    )
}