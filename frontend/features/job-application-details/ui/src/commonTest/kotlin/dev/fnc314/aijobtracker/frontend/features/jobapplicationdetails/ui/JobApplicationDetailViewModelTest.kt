package dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.ui

import dev.fnc314.aijobtracker.frontend.core.data.model.ApplicationChannel
import dev.fnc314.aijobtracker.frontend.core.data.model.Company
import dev.fnc314.aijobtracker.frontend.core.data.model.JobApplication
import dev.fnc314.aijobtracker.frontend.core.artifacts.model.TrackerArtifact
import dev.fnc314.aijobtracker.frontend.core.data.model.TrackerEventType
import dev.fnc314.aijobtracker.frontend.core.data.model.TrackerStatus
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.domain.usecase.GetArtifactSignedUrlUseCase
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.domain.usecase.GetEventsUseCase
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.domain.usecase.LoadArtifactsUseCase
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.domain.usecase.LoadCompaniesUseCase
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.domain.usecase.LoadJobApplicationUseCase
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.domain.usecase.RecordEventUseCase
import dev.fnc314.aijobtracker.frontend.features.jobapplicationdetails.domain.usecase.UpdateApplicationStatusUseCase
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import dev.fnc314.aijobtracker.frontend.core.data.model.TrackerEvent

@OptIn(ExperimentalCoroutinesApi::class)
internal class JobApplicationDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private val sampleApplication = JobApplication(
        id = "app_1",
        company = "Acme Corp",
        role = "Staff Android Engineer",
        location = "Remote",
        status = TrackerStatus.SUBMITTED,
        channel = ApplicationChannel.LINKEDIN,
        jobId = "job_1",
        jobUrl = "https://example.com/job/1",
        description = "Role description",
        tags = persistentListOf("Android", "Kotlin"),
        contactIds = persistentListOf(),
        compensation = null,
        locationDetail = null,
        fitRating = 85,
        fitEvaluation = null,
        counters = null,
        timeline = null,
        notesRendered = "First note\nSecond note",
        createdAt = Instant.fromEpochMilliseconds(1000L),
        updatedAt = Instant.fromEpochMilliseconds(2000L),
        jobApplicationUrl = "https://example.com/app/1",
    )

    private val sampleCompany = Company(
        id = "comp_1",
        name = "Acme Corp",
        aliases = listOf("Acme"),
        website = "https://acme.com",
        industry = "Technology",
        createdAt = Instant.DISTANT_PAST,
        updatedAt = Instant.DISTANT_PAST,
    )

    private class FakeLoadJobApplicationUseCase(
        var application: JobApplication? = null,
        var shouldThrow: Boolean = false,
    ) : LoadJobApplicationUseCase {
        override suspend fun invoke(params: LoadJobApplicationUseCase.Params): JobApplication? {
            if (shouldThrow) throw IllegalStateException("Network failure")
            return if (params.id == application?.id) application else null
        }
    }

    private class FakeLoadCompaniesUseCase(
        var companies: List<Company> = emptyList(),
    ) : LoadCompaniesUseCase {
        override suspend fun invoke(params: LoadCompaniesUseCase.Params): List<Company> = companies
    }

    private class FakeUpdateApplicationStatusUseCase(
        var updatedApplication: JobApplication? = null,
    ) : UpdateApplicationStatusUseCase {
        var lastParams: UpdateApplicationStatusUseCase.Params? = null
        override suspend fun invoke(params: UpdateApplicationStatusUseCase.Params): JobApplication {
            lastParams = params
            return updatedApplication ?: throw IllegalStateException("Not configured")
        }
    }

    private class FakeRecordEventUseCase : RecordEventUseCase {
        var lastRecordedEventId: String? = null
        override suspend fun invoke(params: RecordEventUseCase.Params): String {
            lastRecordedEventId = params.event.id
            return params.event.id
        }
    }

    private class FakeGetArtifactSignedUrlUseCase : GetArtifactSignedUrlUseCase {
        var lastPath: String? = null
        override suspend fun invoke(params: GetArtifactSignedUrlUseCase.Params): String {
            lastPath = params.path
            return "https://signed.url/${params.path}"
        }
    }

    private class FakeLoadArtifactsUseCase(
        var artifacts: List<TrackerArtifact> = emptyList(),
    ) : LoadArtifactsUseCase {
        override suspend fun invoke(params: LoadArtifactsUseCase.Params): List<TrackerArtifact> = artifacts
    }

    private class FakeGetEventsUseCase(
        var events: List<TrackerEvent> = emptyList(),
    ) : GetEventsUseCase {
        override suspend fun invoke(params: GetEventsUseCase.Params): List<TrackerEvent> = events
    }

    private lateinit var loadAppUseCase: FakeLoadJobApplicationUseCase
    private lateinit var loadCompaniesUseCase: FakeLoadCompaniesUseCase
    private lateinit var getEventsUseCase: FakeGetEventsUseCase
    private lateinit var updateStatusUseCase: FakeUpdateApplicationStatusUseCase
    private lateinit var recordEventUseCase: FakeRecordEventUseCase
    private lateinit var getSignedUrlUseCase: FakeGetArtifactSignedUrlUseCase
    private lateinit var loadArtifactsUseCase: FakeLoadArtifactsUseCase
    private lateinit var viewModel: JobApplicationDetailViewModel

    @BeforeTest
    fun setUp(): Unit {
        Dispatchers.setMain(testDispatcher)

        loadAppUseCase = FakeLoadJobApplicationUseCase(application = sampleApplication)
        loadCompaniesUseCase = FakeLoadCompaniesUseCase(companies = listOf(sampleCompany))
        getEventsUseCase = FakeGetEventsUseCase()
        updateStatusUseCase = FakeUpdateApplicationStatusUseCase(
            updatedApplication = sampleApplication.copy(status = TrackerStatus.INTERVIEW)
        )
        recordEventUseCase = FakeRecordEventUseCase()
        getSignedUrlUseCase = FakeGetArtifactSignedUrlUseCase()
        loadArtifactsUseCase = FakeLoadArtifactsUseCase()

        viewModel = JobApplicationDetailViewModel(
            loadApplicationUseCase = loadAppUseCase,
            loadCompaniesUseCase = loadCompaniesUseCase,
            getEventsUseCase = getEventsUseCase,
            updateStatusUseCase = updateStatusUseCase,
            recordEventUseCase = recordEventUseCase,
            getSignedUrlUseCase = getSignedUrlUseCase,
            loadArtifactsUseCase = loadArtifactsUseCase,
        )
    }

    @AfterTest
    fun tearDown(): Unit {
        Dispatchers.resetMain()
    }

    @Test
    fun initialStateIsLoading(): Unit = runTest(testDispatcher) {
        val state = viewModel.uiState.value
        assertEquals(JobApplicationDetailUiState.Loading, state)
    }

    @Test
    fun loadIntentEmitsContentOnSuccess(): Unit = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.uiState.collect() }

        viewModel.onIntent(JobApplicationDetailUiIntent.Load("app_1"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<JobApplicationDetailUiState.Content>(state)
        assertEquals("Acme Corp", state.model.companyName)
        assertEquals("Staff Android Engineer", state.model.role)
        assertEquals(TrackerStatus.SUBMITTED, state.model.status)
        assertEquals(85, state.model.fitScore)
        assertTrue(state.model.timelineEvents.isNotEmpty())
        assertEquals(false, state.isUpdating)
    }

    @Test
    fun loadIntentEmitsErrorWhenNotFound(): Unit = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.uiState.collect() }

        viewModel.onIntent(JobApplicationDetailUiIntent.Load("non_existent"))
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<JobApplicationDetailUiState.Error>(state)
        assertEquals("Application not found", state.message)
    }

    @Test
    fun updateStatusIntentUpdatesModel(): Unit = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.uiState.collect() }

        viewModel.onIntent(JobApplicationDetailUiIntent.Load("app_1"))
        advanceUntilIdle()

        viewModel.onIntent(
            JobApplicationDetailUiIntent.UpdateStatus(
                newStatus = TrackerStatus.INTERVIEW,
                date = null,
                note = "Passed screening",
            )
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<JobApplicationDetailUiState.Content>(state)
        assertEquals(TrackerStatus.INTERVIEW, state.model.status)
        assertEquals("Interview", state.model.statusDisplayText)
        assertEquals(TrackerStatus.INTERVIEW, updateStatusUseCase.lastParams?.status)
        assertEquals("Passed screening", updateStatusUseCase.lastParams?.note)
    }

    @Test
    fun recordEventIntentAppendsTimelineEvent(): Unit = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.uiState.collect() }

        viewModel.onIntent(JobApplicationDetailUiIntent.Load("app_1"))
        advanceUntilIdle()

        val initialEventCount = (viewModel.uiState.value as JobApplicationDetailUiState.Content).model.timelineEvents.size

        viewModel.onIntent(
            JobApplicationDetailUiIntent.RecordEvent(
                type = TrackerEventType.RECRUITER_SCREEN,
                notes = "Screen went very well",
            )
        )
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertIs<JobApplicationDetailUiState.Content>(state)
        assertEquals(initialEventCount + 1, state.model.timelineEvents.size)
        assertTrue(recordEventUseCase.lastRecordedEventId != null)
    }

    @Test
    fun clickBackIntentEmitsNavigateBack(): Unit = runTest(testDispatcher) {
        backgroundScope.launch { viewModel.uiState.collect() }

        viewModel.onIntent(JobApplicationDetailUiIntent.ClickBack)
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals(JobApplicationDetailUiState.NavigateBack, state)
    }
}
