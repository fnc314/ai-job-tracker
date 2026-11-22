package dev.fnc314.aijobtracker.frontend.features.settings.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import dev.fnc314.aijobtracker.frontend.core.designsystem.spacing
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.Res
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_button
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_cancel
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_confirm
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_message
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_title
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_open_settings_button
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_permission_denied_link
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_permission_denied_prefix
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_register_button
import org.jetbrains.compose.resources.stringResource

@Composable
public actual fun PermissionNotificationControl(
    isRegistered: Boolean,
    onRegisterRequested: () -> Unit,
    onDeregisterRequested: () -> Unit,
    modifier: Modifier,
) {
    var stage by remember { mutableStateOf(PermissionUiStage.INITIAL) }
    val permissionRequester = rememberNotificationPermissionRequester()
    val appSettingsOpener = remember { PlatformAppSettingsOpener() }
    var showDeregisterDialog by remember { mutableStateOf(false) }

    if (showDeregisterDialog) {
        AlertDialog(
            onDismissRequest = { showDeregisterDialog = false },
            title = {
                Text(
                    text = stringResource(Res.string.settings_notifications_deregister_dialog_title),
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            text = {
                Text(
                    text = stringResource(Res.string.settings_notifications_deregister_dialog_message),
                    style = MaterialTheme.typography.bodyMedium,
                )
            },
            confirmButton = {
                ElevatedButton(
                    onClick = {
                        showDeregisterDialog = false
                        onDeregisterRequested()
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(stringResource(Res.string.settings_notifications_deregister_dialog_confirm))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeregisterDialog = false },
                    shape = MaterialTheme.shapes.medium,
                ) {
                    Text(stringResource(Res.string.settings_notifications_deregister_dialog_cancel))
                }
            },
        )
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
        if (stage == PermissionUiStage.PERMANENTLY_DENIED) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
            ) {
                Text(
                    text = stringResource(Res.string.settings_notifications_permission_denied_prefix),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                Text(
                    text = stringResource(Res.string.settings_notifications_permission_denied_link),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDecoration = TextDecoration.Underline,
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        appSettingsOpener.openAppSettings()
                    },
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
        ) {
            if (stage == PermissionUiStage.PERMANENTLY_DENIED) {
                ElevatedButton(
                    shape = MaterialTheme.shapes.medium,
                    onClick = { appSettingsOpener.openAppSettings() },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(Res.string.settings_notifications_open_settings_button))
                }
            } else if (!isRegistered) {
                ElevatedButton(
                    shape = MaterialTheme.shapes.medium,
                    onClick = {
                        permissionRequester.requestNotificationPermission { granted ->
                            if (granted) {
                                stage = PermissionUiStage.GRANTED
                                onRegisterRequested()
                            } else {
                                stage = PermissionUiStage.PERMANENTLY_DENIED
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(Res.string.settings_notifications_register_button))
                }
            }

            if (isRegistered) {
                ElevatedButton(
                    shape = MaterialTheme.shapes.medium,
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                    ),
                    onClick = { showDeregisterDialog = true },
                    modifier = Modifier.weight(1f),
                ) {
                    Text(stringResource(Res.string.settings_notifications_deregister_button))
                }
            }
        }
    }
}

