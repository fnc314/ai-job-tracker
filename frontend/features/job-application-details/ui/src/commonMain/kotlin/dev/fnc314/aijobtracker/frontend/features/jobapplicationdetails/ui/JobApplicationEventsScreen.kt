package dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.fnc314.aijobtracker.frontend.core.designsystem.spacing
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.ui.JobApplicationDetailUiIntent
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.ui.JobApplicationDetailUiState
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.ui.component.JetLimeEventsTimeline
import dev.fnc314.aijobtracker.frontend.features_job_application_details_ui.generated.resources.Res
import dev.fnc314.aijobtracker.frontend.features_job_application_details_ui.generated.resources.detail_error_prefix
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

/**
 * Dedicated JetLime Events timeline screen for a job application.
 *
 * Exposes a fullscreen, scroll-optimized vertical timeline view backed by Pushpal Roy's JetLime library.
 *
 * @param applicationId Unique identifier of the job application.
 * @param viewModel [JobApplicationDetailViewModel] supplying the application data.
 * @param modifier Composable layout modifier.
 */
@Composable
public fun JobApplicationEventsScreen(
    applicationId: String,
    viewModel: JobApplicationDetailViewModel = koinInject(),
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(applicationId) {
        viewModel.onIntent(JobApplicationDetailUiIntent.Load(applicationId))
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        when (val state = uiState) {
            is JobApplicationDetailUiState.Loading -> {
                CircularProgressIndicator()
            }
            is JobApplicationDetailUiState.Error -> {
                Text(
                    text = stringResource(Res.string.detail_error_prefix, state.message),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(MaterialTheme.spacing.medium),
                )
            }
            is JobApplicationDetailUiState.Content -> {
                JetLimeEventsTimeline(
                    events = state.model.timelineEvents,
                    modifier = Modifier.fillMaxSize(),
                )
            }
            is JobApplicationDetailUiState.NavigateBack -> {
                // Handled at navigation level
            }
        }
    }
}
