package dev.fnc314.aijobtracker.app.android.navgraph

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavEdge
import com.github.skydoves.navgraph.annotations.NavPreview

/**
 * Deterministic preview theme using the application's brand blue `#2F5597`.
 */
@Composable
public fun AppNavPreviewTheme(content: @Composable () -> Unit) {
  val colors = lightColorScheme(
    primary = Color(0xFF2F5597),
    primaryContainer = Color(0xFFD9E2FF),
    onPrimaryContainer = Color(0xFF001945),
    secondary = Color(0xFF575E71),
    background = Color(0xFFFDFBFF),
    surface = Color(0xFFFDFBFF),
    surfaceVariant = Color(0xFFE1E2EC),
  )
  MaterialTheme(colorScheme = colors, content = content)
}

// -------------------------------------------------------------------------
// 1. JobApplicationListScreen (Root)
// -------------------------------------------------------------------------
@NavEdge(to = JobApplicationDetailsKey::class, label = "View Details")
@NavEdge(to = AddJobApplicationKey::class, label = "Add Lead / Job")
@NavEdge(to = AiApplyKey::class, label = "AI Fast Apply")
@NavEdge(to = CompaniesListKey::class, label = "Companies")
@NavEdge(to = SettingsKey::class, label = "Settings")
@NavDestination(route = JobApplicationListKey::class)
@Composable
public fun JobApplicationListNavScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    NavHeader(title = "Job Applications", subtitle = "12 active tracks")
    NavCard(title = "Senior Android Engineer", subtitle = "Google • Interviewing (Score: 88)")
    NavCard(title = "Staff Mobile Architect", subtitle = "Stripe • Submitted (Score: 92)")
    NavCard(title = "Principal Android Lead", subtitle = "Block • In Draft (Score: 85)")
  }
}

@NavPreview(route = JobApplicationListKey::class, primary = true)
@Preview(showBackground = true)
@Composable
internal fun JobApplicationListPreview() {
  AppNavPreviewTheme { JobApplicationListNavScreen() }
}

// -------------------------------------------------------------------------
// 2. JobApplicationDetailsScreen
// -------------------------------------------------------------------------
@NavEdge(to = ArtifactPreviewKey::class, label = "Preview CV / Letter")
@NavEdge(to = TrackerEditsKey::class, label = "Edit Application")
@NavDestination(route = JobApplicationDetailsKey::class)
@Composable
public fun JobApplicationDetailsNavScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    NavHeader(title = "Application Details", subtitle = "Google — Senior Android Engineer")
    NavCard(title = "Status: Interviewing", subtitle = "Last updated: Today")
    NavCard(title = "Tailored Documents", subtitle = "CV (v3.4.0) • Cover Letter")
    NavCard(title = "Compensation & Notes", subtitle = "$210,000 - $240,000 • Remote (US)")
  }
}

@NavPreview(route = JobApplicationDetailsKey::class, primary = true)
@Preview(showBackground = true)
@Composable
internal fun JobApplicationDetailsPreview() {
  AppNavPreviewTheme { JobApplicationDetailsNavScreen() }
}

// -------------------------------------------------------------------------
// 3. CompaniesListScreen
// -------------------------------------------------------------------------
@NavEdge(to = CompanyDetailsKey::class, label = "Company Profile")
@NavDestination(route = CompaniesListKey::class)
@Composable
public fun CompaniesListNavScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    NavHeader(title = "Target Companies", subtitle = "24 tracked enterprise organizations")
    NavCard(title = "Google", subtitle = "3 Active Applications • Mountain View, CA")
    NavCard(title = "Stripe", subtitle = "1 Active Application • South San Francisco, CA")
    NavCard(title = "PNC Financial Services", subtitle = "Former Client • Pittsburgh, PA")
  }
}

@NavPreview(route = CompaniesListKey::class, primary = true)
@Preview(showBackground = true)
@Composable
internal fun CompaniesListPreview() {
  AppNavPreviewTheme { CompaniesListNavScreen() }
}

// -------------------------------------------------------------------------
// 4. CompanyDetailsScreen
// -------------------------------------------------------------------------
@NavEdge(to = JobApplicationDetailsKey::class, label = "Application")
@NavDestination(route = CompanyDetailsKey::class)
@Composable
public fun CompanyDetailsNavScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    NavHeader(title = "Company Dossier", subtitle = "Google (Alphabet Inc.)")
    NavCard(title = "Engineering Metrics", subtitle = "Tech Stack: Kotlin, Compose, Android OS")
    NavCard(title = "Active Pipeline", subtitle = "Senior Android Engineer (Stage 3)")
  }
}

@NavPreview(route = CompanyDetailsKey::class, primary = true)
@Preview(showBackground = true)
@Composable
internal fun CompanyDetailsPreview() {
  AppNavPreviewTheme { CompanyDetailsNavScreen() }
}

// -------------------------------------------------------------------------
// 5. AiApplyScreen
// -------------------------------------------------------------------------
@NavEdge(to = JobApplicationListKey::class, label = "Save & Return")
@NavDestination(route = AiApplyKey::class)
@Composable
public fun AiApplyNavScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    NavHeader(title = "AI Apply Assistant", subtitle = "Evaluate fit and tailor application")
    NavCard(title = "Job URL Input", subtitle = "https://careers.google.com/jobs/...")
    NavCard(title = "Deterministic + AI Rank", subtitle = "Target Match: 91/100 (Strong Fit)")
    NavCard(title = "Action", subtitle = "Draft Tailored CV & Cover Letter")
  }
}

@NavPreview(route = AiApplyKey::class, primary = true)
@Preview(showBackground = true)
@Composable
internal fun AiApplyPreview() {
  AppNavPreviewTheme { AiApplyNavScreen() }
}

// -------------------------------------------------------------------------
// 6. AddJobApplicationScreen
// -------------------------------------------------------------------------
@NavEdge(to = JobApplicationListKey::class, label = "Saved")
@NavDestination(route = AddJobApplicationKey::class)
@Composable
public fun AddJobApplicationNavScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    NavHeader(title = "New Application", subtitle = "Direct / Manual Ingestion")
    NavCard(title = "Role Title", subtitle = "e.g. Principal Android Engineer")
    NavCard(title = "Company", subtitle = "e.g. Acme Corp")
    NavCard(title = "Initial Status", subtitle = "Lead / Sourced")
  }
}

@NavPreview(route = AddJobApplicationKey::class, primary = true)
@Preview(showBackground = true)
@Composable
internal fun AddJobApplicationPreview() {
  AppNavPreviewTheme { AddJobApplicationNavScreen() }
}

// -------------------------------------------------------------------------
// 7. ArtifactPreviewScreen
// -------------------------------------------------------------------------
@NavDestination(route = ArtifactPreviewKey::class)
@Composable
public fun ArtifactPreviewNavScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    NavHeader(title = "Artifact Previewer", subtitle = "LaTeX CV Rendering")
    NavCard(title = "Franco N. Colaizzi — CV", subtitle = "Compiled via tectonic/pdflatex")
    NavCard(title = "Page 1 of 2", subtitle = "Verified ATS parseable • Zero orphans")
  }
}

@NavPreview(route = ArtifactPreviewKey::class, primary = true)
@Preview(showBackground = true)
@Composable
internal fun ArtifactPreview() {
  AppNavPreviewTheme { ArtifactPreviewNavScreen() }
}

// -------------------------------------------------------------------------
// 8. TrackerEditsScreen
// -------------------------------------------------------------------------
@NavEdge(to = JobApplicationListKey::class, label = "Approved / Dismissed")
@NavDestination(route = TrackerEditsKey::class)
@Composable
public fun TrackerEditsNavScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    NavHeader(title = "Staged Proposals & Edits", subtitle = "Email sweep discoveries")
    NavCard(title = "Proposal: Move Stripe to Interviewing", subtitle = "Confidence: 96%")
    NavCard(title = "Actions", subtitle = "Approve • Edit • Reject")
  }
}

@NavPreview(route = TrackerEditsKey::class, primary = true)
@Preview(showBackground = true)
@Composable
internal fun TrackerEditsPreview() {
  AppNavPreviewTheme { TrackerEditsNavScreen() }
}

// -------------------------------------------------------------------------
// 9. SettingsScreen
// -------------------------------------------------------------------------
@NavEdge(to = SignInKey::class, label = "Sign Out")
@NavDestination(route = SettingsKey::class)
@Composable
public fun SettingsNavScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    NavHeader(title = "Settings", subtitle = "Configuration & Remote Sync")
    NavCard(title = "Sync Backend", subtitle = "Google Cloud Run (ai-job-tracker)")
    NavCard(title = "Push Notifications", subtitle = "FCM Device Token Registered")
    NavCard(title = "Account", subtitle = "fnc314@gmail.com (Connected)")
  }
}

@NavPreview(route = SettingsKey::class, primary = true)
@Preview(showBackground = true)
@Composable
internal fun SettingsPreview() {
  AppNavPreviewTheme { SettingsNavScreen() }
}

// -------------------------------------------------------------------------
// 10. SignInScreen
// -------------------------------------------------------------------------
@NavEdge(to = JobApplicationListKey::class, label = "Authenticated")
@NavDestination(route = SignInKey::class)
@Composable
public fun SignInNavScreen() {
  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp),
  ) {
    NavHeader(title = "Sign In", subtitle = "AI Job Search & Application OS")
    NavCard(title = "Google Provider", subtitle = "Sign in with Google Account")
    NavCard(title = "Local Master Vault", subtitle = "Encrypted Keychain / Biometric Auth")
  }
}

@NavPreview(route = SignInKey::class, primary = true)
@Preview(showBackground = true)
@Composable
internal fun SignInPreview() {
  AppNavPreviewTheme { SignInNavScreen() }
}

// -------------------------------------------------------------------------
// Helper representative UI components
// -------------------------------------------------------------------------
@Composable
private fun NavHeader(title: String, subtitle: String) {
  Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleLarge,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary,
    )
    Text(
      text = subtitle,
      style = MaterialTheme.typography.bodySmall,
      color = MaterialTheme.colorScheme.secondary,
    )
    Spacer(modifier = Modifier.height(4.dp))
  }
}

@Composable
private fun NavCard(title: String, subtitle: String) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(12.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
      )
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.secondary,
      )
    }
  }
}
