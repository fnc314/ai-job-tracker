package dev.fnc314.aijobtracker.frontend.features.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import dev.fnc314.aijobtracker.frontend.core.designsystem.spacing

/**
* Reusable layout scaffold for presenting permission educational rationale and prompt actions.
*
* @param title Header title explaining the permission context.
* @param rationale Educational blurb detailing why the permission is beneficial to the user.
* @param primaryButtonText Label for primary action (e.g. "Grant Permission" or "Enable").
* @param onPrimaryClick Action when primary button is clicked.
* @param secondaryButtonText Optional label for secondary action (e.g. "Open Settings" or "Dismiss").
* @param onSecondaryClick Optional action when secondary button is clicked.
* @param modifier Composable layout modifier.
*/
@Composable
public fun PermissionRequestScreen(
  title: String,
  rationale: String,
  primaryButtonText: String,
  onPrimaryClick: () -> Unit,
  modifier: Modifier = Modifier,
  secondaryButtonText: String? = null,
  onSecondaryClick: (() -> Unit)? = null,
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(MaterialTheme.spacing.medium),
    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      color = MaterialTheme.colorScheme.primary,
    )

    Text(
      text = rationale,
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      modifier = Modifier.semantics {
        liveRegion = LiveRegionMode.Polite
      },
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      Button(
        onClick = onPrimaryClick,
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.weight(1f),
      ) {
        Text(primaryButtonText)
      }

      if (secondaryButtonText != null && onSecondaryClick != null) {
        OutlinedButton(
          onClick = onSecondaryClick,
          shape = MaterialTheme.shapes.medium,
          modifier = Modifier.weight(1f),
        ) {
          Text(secondaryButtonText)
        }
      }
    }
  }
}
