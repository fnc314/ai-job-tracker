package dev.fnc314.aijobtracker.app.android.navgraph

import androidx.navigation3.runtime.NavKey
import com.github.skydoves.navgraph.annotations.NavGraphRoot
import kotlinx.serialization.Serializable

/**
 * Android Navigation Graph model for Job Tracker application destinations.
 *
 * Each destination implements [AppNavKey] and declares its navigation arguments.
 */
@Serializable
public sealed interface AppNavKey : NavKey

/** Start destination: The main applications list. */
@NavGraphRoot
@Serializable
public data object JobApplicationListKey : AppNavKey

/** Details of a specific job application. */
@Serializable
public data class JobApplicationDetailsKey(public val id: String) : AppNavKey

/** Companies directory list. */
@Serializable
public data object CompaniesListKey : AppNavKey

/** Company detail view. */
@Serializable
public data class CompanyDetailsKey(public val companyName: String) : AppNavKey

/** Fast AI-assisted application creator. */
@Serializable
public data object AiApplyKey : AppNavKey

/** Manual job application / lead creator. */
@Serializable
public data object AddJobApplicationKey : AppNavKey

/** Application artifact previewer (e.g., CV / Cover Letter). */
@Serializable
public data class ArtifactPreviewKey(
  public val applicationId: String,
  public val artifactId: String,
) : AppNavKey

/** Batch edits and proposals review destination. */
@Serializable
public data object TrackerEditsKey : AppNavKey

/** Application settings and remote configurations. */
@Serializable
public data object SettingsKey : AppNavKey

/** User authentication / sign-in destination. */
@Serializable
public data object SignInKey : AppNavKey
