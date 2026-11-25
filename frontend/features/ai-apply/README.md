# Feature: AI Apply

> [!NOTE]
> **Quick Facts**
>
> - **Gradle Project IDs**: `:frontend:features-ai-apply-domain`, `:frontend:features-ai-apply-data`, `:frontend:features-ai-apply-ui`
> - **Module Tier**: Feature / User Experience (Clean Architecture: Domain, Data, UI)
> - **Package Base**: `dev.fnc314.aijobtracker.frontend.features.aiapply`
> - **Target Platforms**: Android (`androidTarget`), Desktop JVM (`jvm`), iOS (`iosX64`, `iosArm64`, `iosSimulatorArm64`), Web (`js`, `wasmJs`)
> - **Internal Project Dependencies**:
>   - Domain: `:frontend:core-data`
>   - Data: `:frontend:features-ai-apply-domain`, `:frontend:core-data`, `:frontend:core-network`
>   - UI: `:frontend:features-ai-apply-domain`, `:frontend:features-ai-apply-data`, `:frontend:core-artifacts`, `:frontend:core-widgets`, `:frontend:core-navigation`, `:frontend:core-data`, `:frontend:features-artifact-previewer-ui`

---

## Overview

The `features/ai-apply` module powers the interactive **Apply Assistant** top-level destination (`Edits` / `Pencil` navigation slot). It coordinates dual-tier job fit scoring (instant deterministic `RankScorer` baseline + asynchronous Tier 2 AI semantic calibration), background LaTeX/PDF artifact drafting, Room persistence across process death, and real-time remote application status progression (`RANKED` $\rightarrow$ `DRAFTING` $\rightarrow$ `DRAFTED` $\rightarrow$ `SUBMITTED`).

---

## Architectural Isolation & Design Principles

### Why Client-Side Firebase AI / Vertex AI is Rejected

Integrating direct client-side generative AI SDKs (such as Firebase Vertex AI / Firebase AI Logic) inside `features/ai-apply` is deliberately rejected for four core reasons:

1. **Untrusted Client Boundary & Prompt Exfiltration:**
   - The application drafting pipeline requires private candidate knowledge: full verified employment history, compensation thresholds, behavioral profile, and LaTeX formatting rules (`.agents/skills/job-application-assistant/`).
   - Client-side Gemini invocations require storing system prompts and rules on the client device, exposing proprietary candidate data to decompilation, inspection, and extraction.
2. **Native Tooling & TeX Live Compilation Dependencies:**
   - The apply workflow culminates in compiled PDF artifacts using `lualatex`, `pdflatex`, and `xelatex` with custom classes (`cover.cls`, `main_example.tex`).
   - Client sandboxes (iOS, Android, WebAssembly) cannot execute native TeX distributions or `pdftotext` verification. Artifact generation is strictly a backend workload hosted on Cloud Run.
3. **KMP (Kotlin Multiplatform) Tier Support Fragmentation:**
   - `features/ai-apply` targets Android, Desktop JVM, iOS, and Web (`js`, `wasmJs`).
   - Direct Firebase AI SDKs lack uniform, first-class Kotlin Multiplatform support across JVM Desktop and WebAssembly. Standardizing on Ktor HTTP client calls against backend endpoints (`POST /applications/apply`, `POST /ai/rank`, `POST /artifacts/create`) preserves 100% pure KMP code across all five client targets.
4. **Dual-Tier State Machine & Audit Integrity:**
   - State progression requires deterministic scoring (`RankScorer.evaluate()`, zero-cost, instant `<1s`) before invoking optional Tier 2 semantic AI calibration (`/ai/rank`).
   - Direct client AI calls would fragment state machine transitions, bypassing centralized Firestore event logging and multi-device synchronization.

### Embedding `.agents` Configurations vs. Backend Execution

- **Permissible in Client Apps:** Static candidate profile summaries and declarative prompt schemas may be bundled via Compose Multiplatform Resources (`composeResources`) solely for offline inspection, UI previews, and deterministic rule validation.
- **Strictly External/Backend:** Autonomous agent scripts, CLI subprocesses (e.g. Python scrapers, Composio tools), TeX Live compilers, and secret API tokens are strictly isolated from client binaries and execute exclusively within secure backend pipelines.

---

## Architectural Breakdown & Subprojects

### 1. `:features-ai-apply-domain`

Defines immutable workflow models, repository contract, and SAM use cases:

- `ApplyAssistantRepository`
- `ObserveApplySessionUseCase`, `EvaluateJobPostingUseCase`, `RefineJobEvaluationWithAiUseCase`, `GenerateApplicationArtifactsUseCase`, `ObserveArtifactJobUseCase`, `SyncDraftedArtifactsUseCase`, `ConfirmApplicationSubmissionUseCase`, `ResetApplySessionUseCase`
- `aiApplyDomainModule`

### 2. `:features-ai-apply-data`

Provides local Room persistence, HTTP network endpoints (`/applications/apply`, `/ai/rank`, `/artifacts/create`, `/artifacts/jobs/{jobId}`), on-device HTML fallback metadata parsing, and remote listener/status synchronization:

- `LocalApplyAssistantDataSourceImpl`, `NetworkApplyAssistantDataSourceImpl`, `RemoteApplyAssistantDataSourceImpl`
- `ApplyAssistantRepositoryImpl`
- `aiApplyDataModule`

### 3. `:features-ai-apply-ui`

Renders the single-view interactive chat workflow and MVI coordination:

- `ApplyAssistantViewModel` (`ApplyAssistantUiState`, `ApplyAssistantUiIntent`)
- `AiApplyScreen` (animated dual-tier score transition, interactive Yes/No confirmations, artifact links)
- `AiApplyRoute`, `AiApplyEntryRegistry`, `aiApplyUiModule`, `aiApplyFeatureModule`

---

## Key Gradle Tasks

Run the following commands from the repository root:

```bash
# Run unit tests
./gradlew -p frontend :features-ai-apply-domain:jvmTest \
                      :features-ai-apply-data:jvmTest \
                      :features-ai-apply-ui:jvmTest

# Run Detekt static analysis
./gradlew -p frontend :features-ai-apply-domain:detekt \
                      :features-ai-apply-data:detekt \
                      :features-ai-apply-ui:detekt
```
