package com.android.greenmate.presentation.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.android.greenmate.R

@Composable
fun PermissionDialog(
    modifier: Modifier = Modifier,
    permissionDescriptionProvider: PermissionDescriptionProvider,
    isPermanentlyDeclined: Boolean,
    onDismiss: () -> Unit,
    onOkClick: () -> Unit,
    onGoToAppSettingsClick: () -> Unit,
) {
    val context = LocalContext.current

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        confirmButton = {
            Button(
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                onClick = {
                    if (isPermanentlyDeclined) {
                        onGoToAppSettingsClick()
                    } else {
                        onOkClick()
                    }
                }
            ) {
                Text(
                    text = stringResource(id = R.string.permission_confirm),
                    fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
                    color = Color.Black
                )
            }
        },
        dismissButton = {
            Button(
                colors = ButtonDefaults.buttonColors(Color.Transparent),
                onClick = { onDismiss() }
            ) {
                Text(
                    modifier = Modifier.padding(6.dp),
                    text = stringResource(id = R.string.permission_cancel),
                    fontFamily = FontFamily(Font(R.font.spoqahansansneo_bold)),
                    color = Color.Black
                )
            }
        },
        title = {
            Text(
                text = permissionDescriptionProvider.getTitle(
                    context
                ),
                style = MaterialTheme.typography.titleLarge,
                fontFamily = FontFamily(Font(R.font.dohyeon_regular)),
                color = Color.Black
            )
        },
        text = {
            Text(
                text = permissionDescriptionProvider.getDescription(
                    context,
                    isPermanentlyDeclined = isPermanentlyDeclined
                ),
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = FontFamily(Font(R.font.spoqahansansneo_regular)),
                color = MaterialTheme.colorScheme.secondary
            )
        }
    )
}