package dev.fnc314.aijobtracker.frontend.core.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import dev.fnc314.aijobtracker.frontend.core.designsystem.spacing
import dev.fnc314.aijobtracker.frontend.core_widgets.generated.resources.Res
import dev.fnc314.aijobtracker.frontend.core_widgets.generated.resources.metric_draft
import dev.fnc314.aijobtracker.frontend.core_widgets.generated.resources.metric_interview
import dev.fnc314.aijobtracker.frontend.core_widgets.generated.resources.metric_offer
import dev.fnc314.aijobtracker.frontend.core_widgets.generated.resources.metric_rejected
import dev.fnc314.aijobtracker.frontend.core_widgets.generated.resources.metric_submitted
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * Renders a complete row of application status metric counts paired with a second row of
 * percentage breakdowns underneath in matching semantic colors.
 *
 * Adheres to Material 3 dynamic color scheme and avoids hardcoded color literals:
 * - Draft: [androidx.compose.material3.ColorScheme.onSurfaceVariant]
 * - Submitted: [androidx.compose.material3.ColorScheme.tertiary]
 * - Interviewing: [androidx.compose.material3.ColorScheme.secondary]
 * - Offer: [androidx.compose.material3.ColorScheme.primary]
 * - Rejected: [androidx.compose.material3.ColorScheme.error]
 */
@Composable
public fun CompanyMetricsTextRow(
  draftCount: Int,
  draftPercentage: Float,
  submittedCount: Int,
  submittedPercentage: Float,
  interviewingCount: Int,
  interviewingPercentage: Float,
  offerCount: Int,
  offerPercentage: Float,
  rejectedCount: Int,
  rejectedPercentage: Float,
  modifier: Modifier = Modifier,
) {
  val items = listOf(
    MetricItem(
      label = stringResource(Res.string.metric_draft).uppercase(),
      count = draftCount,
      percentage = draftPercentage,
      content = MaterialTheme.colorScheme.onSurfaceVariant,
      container = MaterialTheme.colorScheme.surface,
    ),
    MetricItem(
      label = stringResource(Res.string.metric_submitted).uppercase(),
      count = submittedCount,
      percentage = submittedPercentage,
      content = MaterialTheme.colorScheme.tertiary,
      container = MaterialTheme.colorScheme.onTertiary,
    ),
    MetricItem(
      label = stringResource(Res.string.metric_interview).uppercase(),
      count = interviewingCount,
      percentage = interviewingPercentage,
      content = MaterialTheme.colorScheme.secondary,
      container = MaterialTheme.colorScheme.onSecondary,
    ),
    MetricItem(
      label = stringResource(Res.string.metric_offer).uppercase(),
      count = offerCount,
      percentage = offerPercentage,
      content = MaterialTheme.colorScheme.primary,
      container = MaterialTheme.colorScheme.onPrimary,
    ),
    MetricItem(
      label = stringResource(Res.string.metric_rejected).uppercase(),
      count = rejectedCount,
      percentage = rejectedPercentage,
      content = MaterialTheme.colorScheme.error,
      container = MaterialTheme.colorScheme.onError,
    ),
  )

  Column(
    modifier = modifier.fillMaxWidth(),
  ) {
    // Row 1: Label and Count
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall, Alignment.CenterHorizontally),
      verticalAlignment = Alignment.CenterVertically,
    ) {
      for (item in items) {
        Column(
          modifier = Modifier
            .weight(1.0F, true)
            .background(color = item.container, shape = MaterialTheme.shapes.small)
            .padding(
              vertical = MaterialTheme.spacing.extraSmall,
              horizontal = MaterialTheme.spacing.extraSmall,
            ),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
        ) {
          AppText(
            text = item.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = item.content,
          )
          AppText(
            text = item.display,
            style = MaterialTheme.typography.bodySmall,
            color = item.content,
          )
        }
      }
    }
  }
}

private data class MetricItem(
  val label: String,
  val count: Int,
  val percentage: Float,
  val content: Color,
  val container: Color = Color.Transparent,
) {
  val percent: String = "${(percentage * 100).roundToInt()}%"
  val display: String = "$count (${percent})"
}
