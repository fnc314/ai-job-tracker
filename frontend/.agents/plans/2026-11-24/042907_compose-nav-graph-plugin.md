# Implementation Plan: Compose Navigation Graph (`compose-nav-graph`) Integration

**Status:** Completed  
**Date:** 2026-11-24  
**Target:** `:frontend:apps:android`  
**Harness:** Antigravity  
**Model:** gemini-2.5-pro  
**Session:** c3a5beb9-c626-4f40-bfe0-df9bfd9fead1  
**Guide Authority:** [`plugin-agent-guides.md`](https://github.com/skydoves/compose-nav-graph/blob/main/plugin-agent-guides.md)  

---

## 1. Executive Summary & Goals

Integrate the [`com.github.skydoves.navgraph`](https://github.com/skydoves/compose-nav-graph) Gradle plugin into the project according to the official [Plugin Agent Guide](https://github.com/skydoves/compose-nav-graph/blob/main/plugin-agent-guides.md).

This will allow our multiplatform and Android application to automatically generate:

1. `build/navgraph/nav-graph.json`: structured navigation model (destinations, arguments, transitions, preview metadata).
2. `build/navgraph/nav-graph.png`: static visual hierarchy graph with real rendered previews.
3. `build/navgraph/nav-graph.html`: interactive visual navigation graph with embedded base64 thumbnails and interactive node exploration.

```mermaid
flowchart TD
    subgraph Feature Modules
        F1[JobApplicationsList]
        F2[JobApplicationDetails]
        F3[CompaniesList]
        F4[CompanyDetails]
        F5[AddJobApplication]
        F6[AiApply]
        F7[ArtifactPreviewer]
        F8[TrackerEdits]
        F9[Settings]
        F10[SignIn]
    end

    subgraph App / NavGraph Target
        NavModel["Navigation Graph Model (Annotated Keys)"]
        Previews["Representative DI-Free Previews (@NavPreview)"]
        Plugin["com.github.skydoves.navgraph (0.3.0)"]
    end

    subgraph Artifact Outputs
        JSON["build/navgraph/nav-graph.json"]
        PNG["build/navgraph/nav-graph.png"]
        HTML["build/navgraph/nav-graph.html"]
    end

    F1 & F2 & F3 & F4 & F5 & F6 & F7 & F8 & F9 & F10 --> NavModel
    NavModel --> Previews
    Previews --> Plugin
    Plugin --> JSON
    Plugin --> PNG
    Plugin --> HTML
```

---

## 2. Architecture & Design Principles

### The Representative Preview Rule (Section 6)

Real composable screens in our application rely on Koin dependency injection (`koinViewModel()`), reactive flows, and Firebase/Firestore repositories. During headless rendering (Layoutlib or Robolectric), DI containers are not initialized.

Following Section 6 of the playbook, we implement representative previews:

- Zero DI, zero ViewModels, zero network calls.
- Deterministic `AppPreviewTheme` wrapped around realistic layouts mimicking each destination.
- Stubs use design tokens matching our Material 3 theme.

---

## 3. Implementation Steps

### A. Dependency Setup

1. In `gradle/libs.versions.toml`:
   - Declare version `navgraph = "0.3.0"`.
   - Declare plugin `navgraph = { id = "com.github.skydoves.navgraph", version.ref = "navgraph" }`.
   - Declare library `compose-navgraph-annotations = { module = "com.github.skydoves.navgraph:annotations", version.ref = "navgraph" }`.

### B. Module Configuration

1. In `frontend/apps/android/build.gradle.kts`:
   - Apply `alias(libs.plugins.ksp)`.
   - Apply `alias(libs.plugins.navgraph)`.
   - Configure `ksp { arg("navgraph.annotatedOnly", "true") }`.
   - Configure `navgraph { renderBackend.set(com.github.skydoves.navgraph.gradle.RenderBackend.AUTO) }`.

### C. Annotated Graph & Representative Previews

1. Create `frontend/apps/android/src/main/kotlin/dev/fnc314/aijobtracker/app/android/navgraph/AppNavGraph.kt`:
   - Annotate all destination keys with `@NavDestination` and `@NavEdge`.
   - Designate `JobApplicationListRoute` with `@NavGraphRoot`.
   - Implement `@NavPreview` functions for all 10 core destinations.

### D. Mise Automation Task

1. Add `.config/mise/tasks/frontend/navgraph.zsh` to generate the interactive HTML and PNG files with a single command (`mise run navgraph`).

---

## 4. Verification Plan & Rendered Output

### 4.1 Verification Checklist

- [x] Configure `gradle/libs.versions.toml` with `navgraph = "0.3.0"` and plugin declarations.
- [x] Configure `frontend/apps/android/build.gradle.kts` with KSP annotations and `com.github.skydoves.navgraph`.
- [x] Annotate navigation destinations in `AppNavKeys.kt` and implement representative DI-free previews in `AppNavScreens.kt`.
- [x] Extract and verify structured graph model: 10 navigation nodes and 14 edges.
- [x] Render preview thumbnails for all 10 core destinations using deterministic preview themes.
- [x] Generate self-contained Mermaid topology flowchart and interactive HTML preview.
- [x] Add automated Mise task: `.config/mise/tasks/frontend/navgraph.zsh`.

### 4.2 Rendered Navigation Topology

The generated topology (`042907_compose-nav-graph.mmd`):

```mermaid
flowchart TD
    N0["AddJobApplicationKey"]
    N1["AiApplyKey"]
    N2["ArtifactPreviewKey"]
    N3["CompaniesListKey"]
    N4["CompanyDetailsKey"]
    N5["JobApplicationDetailsKey"]
    N6["★ JobApplicationListKey (Root)"]
    N7["SettingsKey"]
    N8["SignInKey"]
    N9["TrackerEditsKey"]
    N0 -->|"Saved"| N6
    N1 -->|"Save & Return"| N6
    N3 -->|"Company Profile"| N4
    N4 -->|"Application"| N5
    N5 -->|"Preview CV / Letter"| N2
    N5 -->|"Edit Application"| N9
    N6 -->|"Add Lead / Job"| N0
    N6 -->|"AI Fast Apply"| N1
    N6 -->|"Companies"| N3
    N6 -->|"View Details"| N5
    N6 -->|"Settings"| N7
    N7 -->|"Sign Out"| N8
    N8 -->|"Authenticated"| N6
    N9 -->|"Approved / Dismissed"| N6
```

### 4.3 Evidence & Artifact Assets

The generated artifacts are persisted in this date's plan directory:

- **Structured Navigation JSON:** [`042907_compose-nav-graph.json`](./042907_compose-nav-graph.json)
- **Mermaid Source:** [`042907_compose-nav-graph.mmd`](./042907_compose-nav-graph.mmd)
- **Interactive HTML Graph & Gallery:** [`042907_compose-nav-graph.html`](./042907_compose-nav-graph.html)
- **Sample Root Preview Thumbnail:** [`042907_job-application-list-preview.png`](./042907_job-application-list-preview.png)
- **Full Thumbnails Gallery:** [`042907_thumbs/`](./042907_thumbs/)

---

## 5. Bookkeeping

- [x] Plan persisted at `frontend/.agents/plans/2026-11-24/042907_compose-nav-graph-plugin.md`
- [x] Registry `frontend/.agents/plans/README.md` row updated to `Completed`
- [x] Captured all changed files and evidence assets into a single commit derived from this file name
