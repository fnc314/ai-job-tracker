# Implementation Plan: Core Auth Token Provider, Ktor Bearer Interceptor, Backup Fix & Actionable Settings Link

**Session ID:** `9b874e5b-639a-42eb-a298-970933b2068d`  
**Status:** In Progress  
**Date:** 2026-11-21  
**Target:** `frontend` (`core:auth`, `core:network`, `core:data`, `features:settings`, `apps:android`)  
**Harness:** Antigravity  
**Model:** gemini-3.8-flash  

---

## 1. Problem Statements & Root Causes

1. **Missing Bearer Token on Cloud Run Requests:**
   - In `dev-fnc314-ai-job-tracker`, Cloud Run rejected `POST /notifications/device/register` with `401 Unauthorized` because requests from `NotificationRemoteDataSourceImpl` lacked an `Authorization: Bearer <token>` header.
   - Result: Nothing was recorded in Firestore's `installations/` collection despite the UI reporting success upon OS permission grant.
2. **Settings Restoration on Fresh Install:**
   - On Android, `android:allowBackup="true"` caused Google One / Drive cloud backup to restore stale `job_tracker_prefs.xml` on reinstallation, leading to pre-enabled in-app viewers and non-default theme colors.
3. **Non-Actionable Permission Denied State:**
   - When a user denies notification permissions, the UI displayed static text suggesting "Settings" without an interactive, clickable link that launches `ACTION_APPLICATION_DETAILS_SETTINGS`.

---

## 2. Architecture & Implementation Steps

### 2.1 New Subproject: `frontend/core/auth`
- Create `frontend/core/auth`:
  - `build.gradle.kts`: Declares local KMP library plugins, serialization, DI, and platform Firebase Auth dependencies for Android.
  - Common interface `AuthTokenProvider`:
    ```kotlin
    public interface AuthTokenProvider {
        public suspend fun getIdToken(forceRefresh: Boolean = false): String?
    }
    public expect fun createPlatformAuthTokenProvider(): AuthTokenProvider
    ```
  - Platform implementations:
    - `androidMain`: `FirebaseAuth.getInstance().currentUser?.getIdToken(forceRefresh)?.await()?.token`
    - `jvmMain`, `iosMain`, `jsMain`, `wasmJsMain`: Fallback/local session tokens.
  - Koin DI module `CoreAuthModule.kt` registering `AuthTokenProvider`.

### 2.2 Ktor Bearer Token Interceptor in `frontend/core/network`
- Update `frontend/core/network/build.gradle.kts`: Depend on `projects.coreAuth`.
- In `NetworkClient.kt`:
  - Install a request interceptor in `createJsonHttpClient(authTokenProvider: AuthTokenProvider? = null)` or via Koin injection to attach `Authorization: Bearer <idToken>` if present (and fallback to `X-Api-Key`).

### 2.3 AndroidManifest Backup Fix (`frontend/apps/android`)
- Set `android:allowBackup="false"` in `AndroidManifest.xml` to prevent resurrecting cached preferences on fresh debug installs.

### 2.4 Actionable App Settings Link (`frontend/features/settings`)
- Add `PlatformAppSettingsOpener`:
  ```kotlin
  public expect class PlatformAppSettingsOpener() {
      public fun openAppSettings(): Unit
  }
  ```
  - `androidMain`: Launches `Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}"))`.
  - Common fallback for desktop/web.
- In `SettingsScreen.kt`:
  - Format the denied permission notice using clickable/annotated text with underlined `"Settings"` link invoking `platformAppSettingsOpener.openAppSettings()`.

---

## 3. Verification & Acceptance Criteria
1. `./gradlew :frontend:core-auth:compileKotlinMetadata :frontend:core-network:compileKotlinMetadata :frontend:features-settings-ui:jvmTest :frontend:apps-android:assembleDebug` succeeds.
2. Live test on Galaxy S22 Ultra and Galaxy Tab S9 FE:
   - Fresh install defaults `inAppTextViewer` to false and theme to `#2F5597`.
   - Denying permissions shows the underlined clickable link that opens App Settings.
   - Granting permissions calls Cloud Run with `Authorization: Bearer <idToken>`, returning `200 OK`.
   - Firestore `installations/` in project `dev-fnc314-ai-job-tracker` contains the registered installation document.
