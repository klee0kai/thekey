package com.github.klee0kai.thekey.app.ui.designkit.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.ui.designkit.EmptyScreen
import com.github.klee0kai.thekey.core.ui.devkit.AppTheme
import com.github.klee0kai.thekey.core.ui.devkit.LocalRouter
import com.github.klee0kai.thekey.core.ui.navigation.model.AlertDialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.ConfirmDialogResult
import de.drick.compose.edgetoedgepreviewlib.EdgeToEdgeTemplate
import org.jetbrains.annotations.VisibleForTesting

@Composable
fun AlertDialogScreen(
    dest: AlertDialogDestination = AlertDialogDestination(),
) {
    val router = LocalRouter.current
    val resources = LocalContext.current.resources

    AlertDialog(
        icon = {
            dest.iconRes?.let { iconRes ->
                Icon(painterResource(iconRes), "")
            }
        },
        title = {
            Text(text = dest.title.text(resources))
        },
        text = {
            Text(text = dest.message.text(resources))
        },
        onDismissRequest = {
            router.backWithResult(ConfirmDialogResult.CANCELED)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    router.backWithResult(ConfirmDialogResult.CONFIRMED)
                }
            ) {
                Text(dest.confirm.text(resources))
            }
        },
        dismissButton = {
            dest.reject?.let { reject ->
                TextButton(
                    onClick = { router.backWithResult(ConfirmDialogResult.REJECTED) }
                ) {
                    Text(reject.text(resources))
                }
            }
        }
    )

}

@VisibleForTesting
@Preview
@Composable
fun AlertDialogPreview() = AppTheme {
    AlertDialogScreen()
}


@VisibleForTesting
@Preview(device = Devices.PHONE)
@Composable
fun AlertDialogInScreenPreview() = EdgeToEdgeTemplate {
    AppTheme {
        EmptyScreen()

        AlertDialogScreen()
    }
}

