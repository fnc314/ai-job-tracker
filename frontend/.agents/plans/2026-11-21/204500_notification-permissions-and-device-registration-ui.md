# Implementation Plan: Notification Permissions, Device Push Registration & Koin Startup Race Elimination

**Session ID:** `9b874e5b-639a-42eb-a298-970933b2068d`  
**Status:** Completed  
**Date:** 2026-11-21  
**Target:** `frontend` (`apps:android`, `apps:core`, `core:data`, `features:settings`)  
**Harness:** Antigravity  
**Model:** gemini-3.8-flash  

---

## 1. Executive Summary & Goals

This plan resolves the Android application crash on launch, adds the missing Android 13+ notification runtime permissions, and provides users with explicit controls to force-register or deregister their device for push notifications in the Settings screen.

### Problem Statements:
1. **Startup Race Condition (Crash):** In `MainActivity.onCreate()`, `registerPushTokenIfAvailable()` accessed `PlatformDeviceInfoProvider` and `NotificationRemoteDataSource` via `KoinPlatform.getKoin()`. Because Koin is only booted inside Compose (`App()` -> `KoinApplication { ... }`), async completion of token retrieval before Compose mounts threw `IllegalStateException: KoinApplication has not been started`.
2. **Missing Notification Permissions:** `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />` was absent from `AndroidManifest.xml`. On Android 13+ (API 33+), push notifications were never displayed, and runtime permission was never requested.
3. **No Force-Register UI:** In `@frontend/features/settings/ui`, users had a button to deregister their device from notifications, but no corresponding control to re-register or force registration.

---

## 2. Proposed Architecture & Implementation Steps

### 2.1 Android Manifest & Permissions (`frontend/apps/android`)
- Add `<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />` to `frontend/apps/android/src/main/AndroidManifest.xml`.
- Keep FID mode enabled: `<meta-data android:name="firebase_messaging_installation_id_enabled" android:value="true" />`.
- Clean up `MainActivity.kt`: Remove `registerPushTokenIfAvailable()` and eager `getKoin()` calls from `onCreate()`.

### 2.2 Platform Push Notification Token Abstraction (`frontend/core/data`)
- Define `PlatformPushTokenProvider`:
  ```kotlin
  public interface PlatformPushTokenProvider {
      public suspend fun getPushToken(): String?
  }
  public expect fun createPlatformPushTokenProvider(): PlatformPushTokenProvider
  ```
- Implement actuals:
  - `androidMain`: Retrieves `FirebaseInstallations.getInstance().id.await()`.
  - `iosMain`, `jvmMain`, `jsMain`, `wasmJsMain`: Returns `null`.
- Register `PlatformPushTokenProvider` in `CoreDataModule.kt`.

### 2.3 Settings Domain Layer (`frontend/features/settings/domain`)
- Add `RegisterDeviceNotificationsUseCase`:
  ```kotlin
  public fun interface RegisterDeviceNotificationsUseCase {
      public suspend operator fun invoke(): Boolean
  }
  ```
- Implement `RegisterDeviceNotificationsUseCaseImpl`:
  Fetches `token` from `PlatformPushTokenProvider`, device info from `PlatformDeviceInfoProvider`, and calls `notificationRemoteDataSource.registerDevice(token, platform, deviceId)`.
- Register in `SettingsDomainModule.kt`.

### 2.4 Settings UI Layer (`frontend/features/settings/ui`)
- Update `strings.xml`:
  - `settings_notifications_register_button`: "Register Device for Notifications"
  - `settings_notifications_registered_status`: "Device is registered for push notifications."
- Update `SettingsUiIntent`:
  - Add `SettingsUiIntent.RegisterNotifications`.
- Update `SettingsUiModel`:
  - Track `notificationsRegistered: Boolean` alongside `notificationsDeregistered: Boolean`.
- Update `SettingsViewModel`:
  - Inject `RegisterDeviceNotificationsUseCase` and `DeregisterDeviceNotificationsUseCase`.
  - Handle `SettingsUiIntent.RegisterNotifications`.
  - Update tests in `SettingsViewModelTest.kt`.
- Update `SettingsScreen.kt`:
  - Display buttons for both **Register Device** and **Deregister Device** when `supportsPushNotifications` is true.
  - On Android, trigger `rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission())` for `android.permission.POST_NOTIFICATIONS` upon tapping register.

---

## 3. Verification & Acceptance Criteria
- Unit tests pass: `SettingsViewModelTest` passes with both register and deregister cases.
- No `KoinApplication has not been started` exceptions on cold start on physical/virtual Android devices.
- `adb` logcat confirms successful push token / installation ID registration upon request.


---

## 4. Addendum: Protobuf Schema Authority & Device Registration Status Enumeration

**Added:** 2026-11-21  
**Motivation:** Rather than isolating device notification registration state as ad-hoc booleans or local UI-only enums in `frontend/features/settings/ui`, device status is a persistent entity synced to Firestore (`users/{uid}/devices/{deviceId}`) and queried across backend services and multiplatform clients. It must be codified authoritatively in the canonical protobuf records and mirrored cleanly across KMP modules.

### 4.1 Objectives & Deliverables

1. **Protobuf Authority (`records/proto`):**
   - In `records/proto/src/main/proto/tracker/notification.proto`:
     - Define `enum DeviceRegistrationStatus`:
       - `DEVICE_REGISTRATION_STATUS_UNSPECIFIED = 0;`
       - `DEVICE_REGISTRATION_STATUS_ACTIVE = 1;`
       - `DEVICE_REGISTRATION_STATUS_DEREGISTERED = 2;`
       - `DEVICE_REGISTRATION_STATUS_BANNED = 3;`
       - `DEVICE_REGISTRATION_STATUS_BLOCKED = 4;`
     - Update `message DeviceRegistration` to include:
       - `DeviceRegistrationStatus status = 4;` (and renumber `updatedAt = 5;` or append tag 5).
   - Compile and verify proto generation via `./gradlew :records:records-proto:generateProto :records:records-proto:assemble`.

2. **Backend Persistence & Firestore Sync (`backend/service`):**
   - Update `NotificationRoutes.kt`:
     - In `POST /notifications/device/register`: Store `"status" to DeviceRegistrationStatus.DEVICE_REGISTRATION_STATUS_ACTIVE.name`.
     - In `POST /notifications/device/deregister`: Mark device as `DEVICE_REGISTRATION_STATUS_DEREGISTERED` (or soft-delete/status update) before or alongside document cleanup.

3. **Multiplatform Model Mirroring (`frontend/core/data`):**
   - In `dev.fnc314.aijobtracker.frontend.core.data.model`:
     - Add `@Serializable public enum class DeviceRegistrationStatus` with values corresponding to the proto enum (`UNSPECIFIED`, `ACTIVE`, `DEREGISTERED`, `BANNED`, `BLOCKED`).
   - Move or decouple `DeviceNotificationStatus` out of `frontend:features:settings:ui` into `core:data` domain models.

4. **Settings Feature Alignment (`frontend/features/settings`):**
   - Update `SettingsUiModel`, `SettingsViewModel`, and `SettingsScreen` to reference the canonical `DeviceRegistrationStatus` (alongside transient in-flight UI operation states like `REGISTERING`, `DEREGISTERING`, `FAILED`).
   - Update `SettingsViewModelTest` to validate state transitions against the canonical model.

---

## 5. Addendum: Project-Wide `installationId` Realignment & Root `installations` Collection

**Added:** 2026-11-21  
**Motivation:** Aligning with modern Firebase Installations SDK (FID) by replacing physical `deviceId` with `installationId`, flattening the hierarchy to root `installations/`, and removing `userId` to respect single-user system constraints.

### 5.1 Objectives & Deliverables
1. **Protobuf Realignment (`records/proto`):**
   - Renamed field `deviceId` to `installationId` in `DeviceRegistration` and `DeviceHeartbeat`.
2. **Firestore Security Rules (`firestore.rules`):**
   - Root collection `match /installations/{installationId}` and `match /heartbeats/{heartbeatId}` authored with `isValidInstallation()` and `isValidHeartbeat()`.
3. **Backend Service (`backend/service`):**
   - `NotificationRoutes.kt` routes to root `installations` collection, supporting `installationId` with graceful fallback to `deviceId`.
   - `NotificationDispatchService.kt` queries `installations` collection.
4. **Frontend Core Data & Features (`frontend`):**
   - Updated `PlatformDeviceInfo`, `PlatformDeviceInfoProvider` across all targets (Android, iOS, JVM, JS, Wasm) to provide `installationId`.
   - Updated `NotificationRemoteDataSourceImpl` to send `installationId`.
   - Updated CLI `DevicesCommand.kt` to inspect `installations` collection.
   - Normalized base URL at declaration point in `RemoteNetworkConfig.kt` to eliminate redundant `.trimEnd('/')` call sites.

