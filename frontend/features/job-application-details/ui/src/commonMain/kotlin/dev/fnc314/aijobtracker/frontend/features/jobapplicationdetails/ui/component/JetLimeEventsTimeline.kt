package dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.pushpal.jetlime.EventPointType
import com.pushpal.jetlime.ItemsList
import com.pushpal.jetlime.JetLimeColumn
import com.pushpal.jetlime.JetLimeDefaults
import com.pushpal.jetlime.JetLimeEvent
import com.pushpal.jetlime.JetLimeEventDefaults
import com.pushpal.jetlime.PointPlacement
import dev.fnc314.aijobtracker.frontend.core.data.model.EventActor
import dev.fnc314.aijobtracker.frontend.core.data.model.TrackerEvent
import dev.fnc314.aijobtracker.frontend.core.designsystem.componentSizes
import dev.fnc314.aijobtracker.frontend.core.designsystem.iconSizes
import dev.fnc314.aijobtracker.frontend.core.designsystem.spacing
import dev.fnc314.aijobtracker.frontend.core.widgets.AppText
import dev.fnc314.aijobtracker.frontend.core.widgets.formatRelativeInstant
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.ui.EventDetailDialog
import dev.fnc314.aijobtracker.frontend.features_job_application_details_ui.generated.resources.Res
import dev.fnc314.aijobtracker.frontend.features_job_application_details_ui.generated.resources.data_missing
import dev.fnc314.aijobtracker.frontend.features_job_application_details_ui.generated.resources.timeline_empty
import dev.fnc314.aijobtracker.frontend.features_job_application_details_ui.generated.resources.timeline_event_cd
import dev.fnc314.aijobtracker.frontend.features_job_application_details_ui.generated.resources.timeline_scheduled
import dev.fnc314.aijobtracker.frontend.features_job_application_details_ui.generated.resources.timeline_source_email
import dev.fnc314.aijobtracker.frontend.features_job_application_details_ui.generated.resources.timeline_transition
import dev.fnc314.aijobtracker.frontend.features_job_application_details_ui.generated.resources.timeline_transition_current
import dev.fnc314.aijobtracker.frontend.features_job_application_details_ui.generated.resources.timeline_transition_start
import kotlin.time.Instant
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.stringResource

/**
 * Dedicated JetLime-powered timeline composable.
 *
 * Renders an interactive vertical timeline using [JetLimeColumn] and [JetLimeEvent],
 * designed to be placed directly in a bounded, scroll-root screen layout.
 *
 * @param events The ordered immutable list of application events to render.
 * @param modifier The layout modifier for the timeline container.
 */
@Composable
public fun JetLimeEventsTimeline(
    events: ImmutableList<TrackerEvent>,
    modifier: Modifier = Modifier,
) {
    var selectedEvent by remember { mutableStateOf<TrackerEvent?>(null) }

    if (selectedEvent != null) {
        EventDetailDialog(
            event = selectedEvent!!,
            onDismiss = { selectedEvent = null },
        )
    }

    if (events.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.medium),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(Res.string.timeline_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    val itemsList = remember(events) { ItemsList(events) }
    val columnStyle = JetLimeDefaults.columnStyle(
        contentDistance = MaterialTheme.spacing.medium,
        itemSpacing = MaterialTheme.spacing.small,
        lineThickness = MaterialTheme.componentSizes.timelineLineWidth,
        lineBrush = JetLimeDefaults.lineSolidBrush(color = MaterialTheme.colorScheme.outlineVariant),
    )

    JetLimeColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = MaterialTheme.spacing.medium),
        itemsList = itemsList,
        key = { _, event -> event.id.ifEmpty { "${event.type}_${event.createdAt}" } },
        style = columnStyle,
    ) { _, event, position ->
        val eventCd: String = stringResource(Res.string.timeline_event_cd, event.type.displayName())
        val eventStyle = JetLimeEventDefaults.eventStyle(
            position = position,
            pointPlacement = PointPlacement.CENTER,
            pointColor = MaterialTheme.colorScheme.primaryContainer,
            pointStrokeColor = MaterialTheme.colorScheme.primary,
            pointStrokeWidth = MaterialTheme.componentSizes.timelineLineWidth,
            pointRadius = MaterialTheme.iconSizes.small,
            pointType = EventPointType.custom(
                icon = rememberVectorPainter(event.type.icon),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        )

        JetLimeEvent(
            style = eventStyle,
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.small)
                .clickable { selectedEvent = event }
                .semantics { contentDescription = eventCd },
        ) {
            JetLimeEventCard(event = event)
        }
    }
}

@Composable
private fun JetLimeEventCard(
    event: TrackerEvent,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        shape = MaterialTheme.shapes.small,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.small),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = event.type.displayName(),
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                if (event.actor == EventActor.EMAIL_SYNC) {
                    Surface(
                        shape = MaterialTheme.shapes.extraSmall,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(
                                horizontal = MaterialTheme.spacing.extraSmall,
                                vertical = MaterialTheme.spacing.micro,
                            ),
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                modifier = Modifier.size(MaterialTheme.iconSizes.extraSmall),
                                tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                            Text(
                                text = stringResource(Res.string.timeline_source_email),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(start = MaterialTheme.spacing.micro),
                            )
                        }
                    }
                }
            }

            event.note?.let { note ->
                AppText(
                    text = note,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2,
                    maxLines = 8,
                    modifier = Modifier.wrapContentWidth(
                        align = Alignment.Start,
                    ),
                )
            }

            event.scheduledAt?.let { scheduledAt ->
                if (scheduledAt != Instant.DISTANT_PAST) {
                    Text(
                        text = stringResource(
                            Res.string.timeline_scheduled,
                            scheduledAt,
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                    )
                }
            }

            if (event.statusFrom != null || event.statusTo != null) {
                val transitionFrom = event.statusFrom?.localizedDisplayName()
                    ?: stringResource(Res.string.timeline_transition_start)
                val transitionTo = event.statusTo?.localizedDisplayName()
                    ?: stringResource(Res.string.timeline_transition_current)

                Text(
                    text = stringResource(
                        Res.string.timeline_transition,
                        transitionFrom,
                        transitionTo,
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Text(
                text = formatRelativeInstant(event.createdAt)
                    ?: stringResource(Res.string.data_missing),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
