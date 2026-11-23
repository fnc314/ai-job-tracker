---
framework_version: 1.0.0
---

# Agent Guidelines: AI Job Search

This workspace is structured to manage job search activities, scraper tools, CVs, cover letters, and interview preparation.

## Role

This repo is a job application workspace. The AI agent acts as a career advisor and application assistant for the candidate, helping with:

1. **Job fit evaluation** - Assess job postings against the candidate's profile (skills, experience, behavioral traits)
2. **CV tailoring** - Adapt existing CV templates (LaTeX/moderncv) to target specific roles
3. **Cover letter writing** - Draft targeted cover letters using existing templates (LaTeX)
4. **Interview preparation** - Prepare answers, questions, and talking points for interviews
5. **Career strategy** - Advise on positioning and personal branding

## Repo Structure

- `cv/` - LaTeX CV variants (moderncv template, banking style)
- `cover_letters/` - LaTeX cover letters (custom cover.cls template)
- `.claude/skills/job-application-assistant/` - Methodology reference files and Candidate Profile
- `.claude/commands/` - Workflow commands and shared rules for the application process
- `.agents/skills/` - Job search CLI tools

## Canonical Workflow Specifications

The step-by-step instructions and triggers for tasks (setup, scrape, rank, apply, upskill, interview) are defined in the `.claude/commands/` directory.

- Read `.claude/skills/job-application-assistant/01-candidate-profile.md` for the candidate's detailed identity, education, experience, and technical skills.
- Read `.claude/commands/_shared.md` for the core workflow and the mandatory Verification Checklist for all generated documents.
- Portal Search Skills: Job-portal search CLIs live under `.agents/skills/` in the portable Agent Skills format.

## Coding Style & Architecture Standards (MANDATORY)

### 1. KDoc Requirement for Public Declarations

- **All `public` declarations** (classes, interfaces, objects, companion objects, functions, and properties) must have descriptive, complete KDoc documentation.
- **Expect / Actual Declarations**: Both `expect` and `actual` declarations require full KDoc documentation explaining their platform-agnostic contract and platform-specific behaviors.
- **Interfaces & API Contracts**: All `interface` definitions and API-exposed members must document behavior, parameters, and return types. This is verified continuously via Konsist architectural tests.

### 2. Compose Modifier & Formatting Conventions

- **Modifier Parameter**: Composable functions that emit UI must accept a `modifier: Modifier = Modifier` parameter placed as the first optional parameter.
  - In Kotlin Multiplatform (KMP), specify the default value (`modifier: Modifier = Modifier`) on the `expect fun`, and omit default values on `actual fun` (inherited per Kotlin compiler rules).
- **Two-Space Indentation**: All Kotlin and build script code must use 2-space indentation, adhering to root `.editorconfig`.
- **Global Theme Tokens (No Literal Constants)**:
  - Do NOT hardcode literal sizes, spacings, or dimensions (e.g. `16.dp`, `24.dp`, `20.dp`) in `Modifier` chains or layout arrangements.
  - Migrate and bind all layout dimensions to centralized design tokens:
    - Spacing / Padding: `MaterialTheme.spacing.<token>` (`micro`, `small`, `medium`, `large`, etc.).
    - Icon / Component Sizes: `MaterialTheme.iconSizes.<token>` or `MaterialTheme.componentSizes.<token>`.
    - Typography & Colors: `MaterialTheme.typography.*` and `MaterialTheme.colorScheme.*`.

### 3. Static Analysis & Verification

- **Detekt**: Detekt is configured across all subprojects via convention plugins referencing the central rules in `config/detekt/detekt-*.yml`. Run `mise run detekt` before committing.
- **Konsist**: Konsist architectural tests under `tests/` enforce visibility, KDoc coverage, naming conventions, and layer boundaries.

## Candidate Profile

### Identity

- **Name:** [YOUR_NAME]
- **Location:** [CITY, COUNTRY]
- **Languages:** [LANGUAGES]
- **CV language:** [LANGUAGE]

- **Status:** [EMPLOYMENT_STATUS]
- **LinkedIn headline:** "[HEADLINE]"
- **LinkedIn:** [URL]
- **GitHub:** [URL]
- **Email:** [EMAIL]

### Education

- **[DEGREE]** ([YEARS]) - [UNIVERSITY]
  - [NOTES]

### Professional Experience

- **[TITLE]** ([DATES]) - **[COMPANY]** ([LOCATION])
  - [BULLET_POINT]
  - [BULLET_POINT]

### Technical Skills

- **Primary:** [SKILLS]
- **Secondary:** [SKILLS]
- **Domain:** [DOMAINS]
- **Software:** [SOFTWARE]

### Certifications

- **[CERT_NAME]** - [ISSUER] - completed [DATE]

### Publications

- [PUBLICATION_DETAILS]

### Awards

- [AWARD_DETAILS]

### Behavioral Profile

- **[TRAIT]** - [DESCRIPTION]
- **Strengths:** [STRENGTHS]
- **Growth areas:** [WEAKNESSES]
- **Thrives in:** [ENVIRONMENTS]

### What Excites You

- [INTEREST_1]
- [INTEREST_2]

### Target Sectors

- [SECTOR_1]
- [SECTOR_2]

### Deal-breakers

- [DEAL_BREAKER_1]
- [DEAL_BREAKER_2]
