# Plan: Multiplatform Permission Request & Notification Control Architecture

- **Date:** 2026-11-21
- **Status:** Completed
- **Author:** Franco N. Colaizzi

## Goal & Motivation
Currently, permission handling is embedded ad-hoc directly within `SettingsScreen.kt` using an Android permission launcher callback. On Android, runtime permissions (like `POST_NOTIFICATIONS`) follow a distinct multi-stage lifecycle:
1. **Initial Request**: First prompt opportunity (`shouldShowRequestPermissionRationale == false`).
2. **First Denial / Rationale Stage**: Second opportunity. The platform allows re-prompting, but guidelines dictate displaying educational rationale explaining why the permission is beneficial before prompting again.
3. **Permanent Denial ("Don't ask again" / 2nd Denial)**: Subsequent prompt invocations are suppressed by the OS. The only avenue for user activation is directing them to OS App Details Settings.

On iOS, there is only 1 system dialog opportunity. Other platforms (JVM, Web/WasmJs) do not have the same runtime permission dialog model.

We built an extensible multiplatform `PermissionRequestScreen` and `PermissionNotificationControl` pattern using `expect`/`actual` so:
- Common UI code renders a clean, singular `PermissionNotificationControl` component.
- The control seamlessly adapts its UI, messaging, accessibility announcements, and button callbacks (Request Permission vs. Open Device Settings) according to platform requirements and current denial/rationale states.
- On Android, the Rationale flow displays clear explanatory rationale above the action buttons with proper TalkBack accessibility announcements (`Modifier.semantics { liveRegion = LiveRegionMode.Polite }`).
- Deep-links to OS App Notification Settings highlight/twinkle the target setting using Android platform extras `:settings:fragment_args_key` (`notifications` / `permission_settings`) and `:settings:show_fragment_args`.
- Uses `LifecycleResumeEffect(Unit)` to automatically re-query permission status on `ON_RESUME` and auto-register when returning from Settings.
- Adheres to Material 3 standard button primitives: uses `ElevatedButton` for primary/elevated actions that stand out in Dark Mode, and `OutlinedButton` for secondary / dialog dismissal actions.
- Only displays the "Register" button when the device is not yet registered; once granted and registered, the register button is hidden because "we have what we want".
- Implements a confirmation roadblock `AlertDialog` before executing device deregistration, utilizing standard Material 3 `ElevatedButton` (Confirm/Deregister with error container) and `OutlinedButton` (Dismiss/Cancel).
- Replaces legacy/deprecated Firebase Messaging `onNewToken` with modern V1 `onRegistered(installationId)` and `onUnregistered(installationId)`.

---

## Architectural Breakdown

### 1. Multiplatform Permission State Model (`features/settings/ui`)
Defined clean permission lifecycle stages in Common:
```kotlin
public enum class PermissionUiStage {
    INITIAL,
    RATIONALE,
    PERMANENTLY_DENIED,
    GRANTED,
}
```

### 2. Expect / Actual `PermissionNotificationControl`
Common composable specification:
```kotlin
@Composable
public expect fun PermissionNotificationControl(
    isRegistered: Boolean,
    onRegisterRequested: () -> Unit,
    onDeregisterRequested: () -> Unit,
    modifier: Modifier = Modifier,
)
```

- **Android (`androidMain`)**:
  - Leverages `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())`.
  - Checks `ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)`.
  - Tracks first denial vs. rationale vs. permanent denial using `ActivityCompat.shouldShowRequestPermissionRationale`.
  - Auto-refreshes state on `LifecycleResumeEffect(Unit)`.
  - Uses `PlatformAppSettingsOpener` with `:settings:fragment_args_key` and `:settings:show_fragment_args` for target setting highlighting.
  - Shows `ElevatedButton` for actions and confirmation roadblock `AlertDialog` before deregistration.

- **iOS (`iosMain`)**:
  - Handles single prompt opportunity. If denied, transitions straight to device settings navigation.
  - Implements deregistration roadblock `AlertDialog` with `ElevatedButton` and `OutlinedButton`.

- **Desktop JVM / Web / WasmJs (`jvmMain`, `jsMain`, `wasmJsMain`)**:
  - Provides conditional `ElevatedButton` registration/deregistration and standard `AlertDialog` roadblock.

### 3. Reusable `PermissionRequestScreen` Scaffold
Created `PermissionRequestScreen.kt` for dedicated full-page permission onboarding or focused permission prompt screens, hosting rationale text with polite live region semantics and `ElevatedButton` primary action.

### 4. Top-Level Cards & Universal Widgets
Updated `TopLevelCardActionsGrid` (used across Applications and Companies tiles) to style primary actions with `ElevatedButton`, adhering to the Material 3 standard design hierarchy.

---

## Verification & Testing
1. **Compilation & Unit Tests**:
   - Ran `mise exec -- ./gradlew :frontend:features-settings-ui:jvmTest :frontend:core-widgets:jvmTest` (Passed 100%).
2. **Android Build & Installation**:
   - Ran `mise exec -- ./gradlew :frontend:apps-android:installDebug` and installed cleanly on both Galaxy S22 Ultra (`SM-S908U1`) and Galaxy Tab S10 FE (`SM-X520`) with 0 deprecation warnings.
3. **End-to-End Registration Flow**:
   - Enabled notifications -> `PermissionNotificationControl` transitions to `GRANTED` state.
   - Register button only displayed when not registered; hidden once registered.
   - Roadblock dialog gates deregistration.

