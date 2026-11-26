---
name: greenhouse-search
description: Search job postings across company career boards hosted on Greenhouse (e.g., Duolingo, Figma, Stripe, Airbnb, Reddit, Robinhood, Gusto). Use to find engineering, Android, mobile, or tech jobs directly at specific firms.
version: 1.0.0
---

# greenhouse-search

Searches public Greenhouse ATS job boards directly via the official unauthenticated Greenhouse Job Board API.

## Commands

- `search`: Search job listings across one or more company board tokens.
  - `--query`, `-q <keywords>`: Match keywords in job title or description.
  - `--board`, `-b <token>`: Target Greenhouse board token (e.g. `duolingo`, `figma`, `reddit`). Can be repeated or comma-separated. Default: standard tech target boards.
  - `--location`, `-l <location>`: Filter by location (e.g. `Pittsburgh`, `Remote`, `New York`).
  - `--jobage <days>`: Filter postings updated within the last N days.
  - `--limit <n>`: Maximum number of results (default: 20).
  - `--format <json|table|plain>`: Output format (default: `json`).
- `detail <board> <job_id>`: Fetch full posting description and requirements.

## Examples

```bash
# Search Android roles at Duolingo
bun run .agents/skills/greenhouse-search/cli/src/cli.ts search -q "Android" -b duolingo --format table

# Search Staff / Principal roles across multiple companies
bun run .agents/skills/greenhouse-search/cli/src/cli.ts search -q "Principal" -b "duolingo,figma,reddit,stripe" --format table

# Fetch job details
bun run .agents/skills/greenhouse-search/cli/src/cli.ts detail duolingo 8851677002 --format plain
```
