package dev.fnc314.aijobtracker.frontend.features.settings.ui

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleResumeEffect
import dev.fnc314.aijobtracker.frontend.core.designsystem.spacing
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.Res
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_button
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_cancel
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_confirm
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_message
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_deregister_dialog_title
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_enable_button
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_open_settings_button
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_permission_denied_link
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_permission_denied_prefix
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_permission_rationale_text
import dev.fnc314.aijobtracker.frontend.features_settings_ui.generated.resources.settings_notifications_register_button
import org.jetbrains.compose.resources.stringResource

private fun Context.findActivity(): Activity? {
  var current = this
  while (current is ContextWrapper) {
    if (current is Activity) return current
    current = current.baseContext
  }
  return null
}

private fun checkNotificationPermission(context: Context): Boolean {
  return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    ContextCompat.checkSelfPermission(
      context,
      Manifest.permission.POST_NOTIFICATIONS,
    ) == PackageManager.PERMISSION_GRANTED
  } else {
    true
  }
}

/**
 * Android actual implementation of [PermissionNotificationControl].
 *
 * Handles runtime notification permissions (`POST_NOTIFICATIONS` on API 33+),
 * educational rationale, settings intent redirects on permanent denial,
 * and registration/deregistration confirmation flows.
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
  val context = LocalContext.current
  val appSettingsOpener = remember { PlatformAppSettingsOpener() }
  var showDeregisterDialog by remember { mutableStateOf(false) }

  var stage by remember {
    mutableStateOf(
      if (checkNotificationPermission(context)) PermissionUiStage.GRANTED else PermissionUiStage.INITIAL
    )
  }

  // Automatically re-query permission state whenever user returns from Settings or foregrounds the app
  LifecycleResumeEffect(Unit) {
    val currentlyGranted = checkNotificationPermission(context)
    if (currentlyGranted) {
      val wasNotGranted = stage != PermissionUiStage.GRANTED
      stage = PermissionUiStage.GRANTED
      if (wasNotGranted && !isRegistered) {
        onRegisterRequested()
      }
    } else if (stage == PermissionUiStage.GRANTED) {
      // Permission was revoked in settings while app was backgrounded
      stage = PermissionUiStage.PERMANENTLY_DENIED
    }
    onPauseOrDispose {}
  }

  val launcher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission(),
  ) { granted: Boolean ->
    if (granted) {
      stage = PermissionUiStage.GRANTED
      onRegisterRequested()
    } else {
      val activity = context.findActivity()
      val shouldShowRationale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && activity != null) {
        ActivityCompat.shouldShowRequestPermissionRationale(
          activity,
          Manifest.permission.POST_NOTIFICATIONS,
        )
      } else {
        false
      }

      stage = if (shouldShowRationale) {
        PermissionUiStage.RATIONALE
      } else {
        PermissionUiStage.PERMANENTLY_DENIED
      }
    }
  }

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

  Column(
    modifier = modifier.fillMaxWidth(),
    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
  ) {
    when (stage) {
      PermissionUiStage.RATIONALE -> {
        Text(
          text = stringResource(Res.string.settings_notifications_permission_rationale_text),
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          modifier = Modifier.semantics {
            liveRegion = LiveRegionMode.Polite
          },
        )
      }

      PermissionUiStage.PERMANENTLY_DENIED -> {
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

      PermissionUiStage.INITIAL,
      PermissionUiStage.GRANTED -> {
        // No extra banner text
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
    ) {
      when (stage) {
        PermissionUiStage.INITIAL -> {
          if (!isRegistered) {
            Button(
              shape = MaterialTheme.shapes.medium,
              onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                  launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                  stage = PermissionUiStage.GRANTED
                  onRegisterRequested()
                }
              },
              modifier = Modifier.weight(1f),
            ) {
              Text(stringResource(Res.string.settings_notifications_register_button))
            }
          }
        }

        PermissionUiStage.RATIONALE -> {
          if (!isRegistered) {
            Button(
              shape = MaterialTheme.shapes.medium,
              onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                  launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                  stage = PermissionUiStage.GRANTED
                  onRegisterRequested()
                }
              },
              modifier = Modifier.weight(1f),
            ) {
              Text(stringResource(Res.string.settings_notifications_enable_button))
            }
          }
        }

        PermissionUiStage.PERMANENTLY_DENIED -> {
          Button(
            shape = MaterialTheme.shapes.medium,
            onClick = { appSettingsOpener.openAppSettings() },
            modifier = Modifier.weight(1f),
          ) {
            Text(stringResource(Res.string.settings_notifications_open_settings_button))
          }
        }

        PermissionUiStage.GRANTED -> {
          if (!isRegistered) {
            Button(
              shape = MaterialTheme.shapes.medium,
              onClick = onRegisterRequested,
              modifier = Modifier.weight(1f),
            ) {
              Text(stringResource(Res.string.settings_notifications_register_button))
            }
          }
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
}

