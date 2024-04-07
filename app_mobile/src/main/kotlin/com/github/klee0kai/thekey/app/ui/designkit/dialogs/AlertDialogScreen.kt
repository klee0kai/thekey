package com.github.klee0kai.thekey.app.ui.designkit.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.github.klee0kai.thekey.app.ui.designkit.LocalRouter
import com.github.klee0kai.thekey.app.ui.navigation.model.AlertDialogDestination
import com.github.klee0kai.thekey.app.ui.navigation.model.ConfirmDialogResult

@Composable
@Preview
fun AlertDialogScreen(
    dest: AlertDialogDestination = AlertDialogDestination(),
) {
    val router = LocalRouter.current
    val resources = LocalContext.current.resources

    AlertDialog(
        icon = {
            if (dest.iconRes != null) {
                Icon(painterResource(dest.iconRes), "")
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
            if (dest.reject != null) {
                TextButton(
                    onClick = { router.backWithResult(ConfirmDialogResult.REJECTED) }
                ) {
                    Text(dest.reject.text(resources))
                }
            }
        }
    )

}