package dev.fnc314.aijobtracker.frontend.features.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.fnc314.aijobtracker.frontend.core.designsystem.spacing
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.Res
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_button
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_cancel
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_confirm
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_message
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_title
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_register_button
import org.jetbrains.compose.resources.stringResource

/**
 * JavaScript / Web actual implementation of [PermissionNotificationControl].
 *
 * Provides notification registration and deregistration controls with confirmation dialog.
 *
 * @param isRegistered Whether this device is registered for notifications.
 * @param onRegisterRequested Callback invoked when the user requests registration.
 * @param onDeregisterRequested Callback invoked when the user confirms deregistration.
 * @param modifier Composable layout modifier.
 */
@Composable
public actual fun PermissionNotificationControl(
  isRegistered: Boolean,
  onRegisterRequested: () -> Unit,
  onDeregisterRequested: () -> Unit,
  modifier: Modifier,
) {
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
        Button(
          onClick = {
            showDeregisterDialog = false
            onDeregisterRequested()
          },
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

  Row(
    modifier = modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
  ) {
    if (!isRegistered) {
      Button(
        shape = MaterialTheme.shapes.medium,
        onClick = onRegisterRequested,
        modifier = Modifier.weight(1f),
      ) {
        Text(stringResource(Res.string.settings_notifications_register_button))
      }
    }
    if (isRegistered) {
      FilledTonalButton(
        shape = MaterialTheme.shapes.medium,
        onClick = { showDeregisterDialog = true },
        modifier = Modifier.weight(1f),
      ) {
        Text(stringResource(Res.string.settings_notifications_deregister_button))
      }
    }
  }
}

