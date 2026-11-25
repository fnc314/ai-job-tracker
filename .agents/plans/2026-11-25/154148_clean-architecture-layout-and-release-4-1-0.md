# Plan: Clean Architecture Layout Alignment & 4.1.0 Release

- **Status:** Completed
- **Created:** 2026-11-25
- **Completed:** 2026-11-25
- **Branch:** `main`
- **Release Version:** `4.1.0` (Tagged `version/4.1.0`)

---

## 1. Executive Summary & Goals

This plan establishes a pristine Clean Architecture modularization across all 10 feature collections by introducing dedicated `:features:<name>:module` modules:

1. **Clean Architecture Boundary Across Feature Collections**:
   - `UI -> Domain`
   - `Data -> Domain` (Implements repository interfaces defined in domain)
   - `UI` has NO direct dependency on `Data`
   - Pure Kotlin business logic in Domain (Entities, Repository interfaces, Use Cases).
   - Pure data plumbing in Data (Repository implementations, data sources, network/storage DTOs).
2. **Dedicated `:features:<name>:module` Assembly Modules**:
   - Created 10 dedicated `:features:<name>:module` subprojects (`add-job-application`, `ai-apply`, `documents-viewer` [formerly `artifact-previewer`], `companies-list`, `company-details`, `job-application-details`, `job-applications-list`, `settings`, `sign-in`, `tracker-edits`).
   - Renamed `:frontend:features:artifact-previewer` to `:frontend:features:documents-viewer` across all tiers (`data`, `domain`, `ui`, `module`) to mirror `:frontend:core:documents`.
   - Wires up the DI definitions and exposes one consolidated, cohesive `public val <name>Module: Module` per feature.
   - Relocated DI container tests into `:features:<name>:module`.
3. **Encapsulate Implementation Details from `:apps:core`**:
   - `:apps:core` depends **only** on `:features:<name>:module` for each feature.
   - Implementation details and third-party data layer SDKs remain completely concealed from the composite core app.
4. **Harden `mise run release` & Release 4.1.0**:
   - Updated `.config/mise/tasks/admin/release.zsh` to synchronize both `gradle/local.versions.toml` and root `gradle.properties`.
   - Staged and committed all changes, executed `mise run release 4.1.0 --no-push`, tagged `version/4.1.0`.

---

## 2. Architecture & Design Principles

### Feature Modularization Layout (4 Tiers per Feature)

```
┌────────────────────────────────────────────────────────────────────────┐
│                        :features:<name>:di                             │
│  - Sole dependency on Koin DSL (libs.plugins.local.dependencies.koin)  │
│  - Imports Domain, Data, and UI to wire them together                  │
│  - Exports a single top-level public val <name>Module: Module          │
└──────────────┬────────────────────────┬────────────────────────┬───────┘
               │ api/implementation     │ implementation         │ implementation
               ▼                        ▼                        ▼
      ┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
      │ :feature:<name> │      │ :feature:<name> │      │ :feature:<name> │
      │      :ui        │      │     :domain     │      │      :data      │
      │                 │      │                 │      │                 │
      │ - Screens       │      │ - Entities      │      │ - Repositories  │
      │ - ViewModels    │      │ - Repositories  │      │ - DataSources   │
      │ - UiState/Intent│      │   (Interfaces)  │      │ - DTOs & Mappers│
      │ - NO DI DSL     │      │ - UseCases      │      │ - Third-party   │
      │                 │      │ - Pure Kotlin   │      │   SDKs          │
      │                 │      │ - NO DI DSL     │      │ - NO DI DSL     │
      └────────┬────────┘      └────────▲────────┘      └────────┬────────┘
               │                        │                        │
               │ depends on             │ implements             │
               └────────────────────────┴────────────────────────┘
```

### Dependency Rules

- **`domain`**: Zero dependencies on Koin, Android, Data, or UI. Pure Kotlin Multiplatform.
- **`data`**: Implements `domain` repository contracts. Zero dependencies on Koin. Third-party SDKs (Firebase, Ktor, Room) are private `implementation` dependencies here.
- **`ui`**: Depends on `domain`, `core:widgets`, `core:navigation`, and `core:data`. Zero dependencies on `data` or Koin DSL definitions.
- **`di`**: The meeting ground where `*RepositoryImpl` meets `*Repository`, UseCases are bound, and ViewModels are registered.
- **`:apps:core`**: Merely includes the 10 feature `*Module`s without knowing any feature's internal data sources or SDKs.

---

## 3. Proposed Changes & Implementation Steps

### Step 1: Create `:features:<feature>:di` Modules

For each of the 10 features:

1. Create directory `frontend/features/<feature>/di/`
2. Create `frontend/features/<feature>/di/build.gradle.kts`:

   ```kotlin
   plugins {
       alias(libs.plugins.local.kotlin.multiplatform)
       alias(libs.plugins.local.android.kmp.library)
       alias(libs.plugins.local.dependencies.di)
       alias(libs.plugins.local.dependencies.koin)
   }

   kotlin {
       sourceSets {
           commonMain.dependencies {
               api(projects.features.<featureCamel>.ui)
               api(projects.features.<featureCamel>.domain)
               implementation(projects.features.<featureCamel>.data)
               implementation(projects.core.data)
           }
           commonTest.dependencies {
               implementation(libs.kotlin.test)
               implementation(libs.koin.test)
           }
       }
   }
   ```

3. Move/unify the Koin definitions into `frontend/features/<feature>/di/src/commonMain/kotlin/.../<Feature>Module.kt`:
   - Declare `public val <feature>Module: Module = module { ... }` binding data sources, repositories, use cases, and view models.
4. Remove the obsolete `*DataModule.kt`, `*DomainModule.kt`, and `*UiModule.kt` from `data/`, `domain/`, and `ui/`.
5. Remove `alias(libs.plugins.local.dependencies.koin)` and `alias(libs.plugins.local.dependencies.di)` from `domain/build.gradle.kts` and `data/build.gradle.kts`.

### Step 2: Register New Modules in `gradle/local.versions.toml`

Add the 10 new `di` modules to `[libraries]` and `[bundles]` in `gradle/local.versions.toml`:

```toml
frontend-features-add-job-application-di = { module = "dev.fnc314.aijobtracker.features.add-job-application.di", version.ref = "version" }
... (10 entries)
```

And add them to bundle `frontend-features`.

### Step 3: Simplify `:apps:core`

In `frontend/apps/core/build.gradle.kts`:

- Replace all 30 individual feature dependencies with the 10 `:di` dependencies:

  ```kotlin
  implementation(projects.features.signIn.di)
  implementation(projects.features.jobApplicationsList.di)
  implementation(projects.features.jobApplicationDetails.di)
  implementation(projects.features.companiesList.di)
  implementation(projects.features.companyDetails.di)
  implementation(projects.features.settings.di)
  implementation(projects.features.addJobApplication.di)
  implementation(projects.features.aiApply.di)
  implementation(projects.features.artifactPreviewer.di)
  implementation(projects.features.trackerEdits.di)
  ```

In `frontend/apps/core/src/commonMain/kotlin/.../AppDI.kt`:

- Include only the 10 unified feature modules:

  ```kotlin
  public val appModule: Module = module {
      includes(
          coreLoggingModule,
          coreAuthModule,
          coreDataModule,
          coreNavigationModule,
          signInModule,
          jobApplicationsListModule,
          jobApplicationDetailsModule,
          companiesListModule,
          companyDetailsModule,
          settingsModule,
          addJobApplicationModule,
          aiApplyModule,
          artifactPreviewerModule,
          trackerEditsModule,
      )
  }
  ```

### Step 4: Harden `.config/mise/tasks/admin/release.zsh`

Update `release.zsh` to synchronize `gradle.properties` alongside `gradle/local.versions.toml`.

### Step 5: Commit & Execute `mise run release 4.1.0 --no-push`

- Commit architectural refactor.
- Run release script to bump to 4.1.0, commit, and tag locally.

---

## 4. Verification Plan & Testing Checklist

### Automated Tests

1. **Compile & Unit Test Verification**:

   ```bash
   ./gradlew :frontend:features:settings:di:jvmTest \
             :frontend:features:sign-in:di:jvmTest \
             :frontend:features:companies-list:ui:jvmTest \
             :frontend:features:company-details:ui:jvmTest \
             :frontend:features:job-applications-list:ui:jvmTest --console=plain
   ```

2. **Full Monorepo Build**:

   ```bash
   ./gradlew :buildAll --console=plain
   ```

3. **Dokka Documentation Generation**:

   ```bash
   ./gradlew :frontend:core:auth:dokkaGeneratePublicationHtml \
             :frontend:core:documents:dokkaGeneratePublicationHtml \
             :frontend:features:settings:di:dokkaGeneratePublicationHtml --console=plain
   ```

4. **Architecture Tests (Konsist)**:

   ```bash
   ./gradlew :tests:architecture:test --console=plain
   ```

5. **Verify Version Files & Git Tag**:

   ```bash
   grep 'version = "4.1.0"' gradle/local.versions.toml
   grep 'version=4.1.0' gradle.properties
   git tag -l "version/4.1.0"
   ```

---

## 5. Bookkeeping

- [x] Plan persisted at `.agents/plans/2026-11-25/154148_clean-architecture-layout-and-release-4-1-0.md`
- [x] Registry `README.md` row added / status synchronized
- [ ] Craft a single commit of all changed files
- [ ] Stage changed files and craft a commit message based on the name of this file
- [ ] Push `git push origin main` (deferred / local tag only as requested)
