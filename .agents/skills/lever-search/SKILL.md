---
name: lever-search
description: Search job postings across company career boards hosted on Lever ATS (e.g., Spotify, Palantir, Affirm, Twitch, etc.). Use to find engineering, Android, mobile, or tech jobs directly at specific firms.
version: 1.0.0
---

# lever-search

Searches public Lever ATS job boards directly via the official unauthenticated Lever Postings REST API.

## Commands

- `search`: Search job listings across one or more company board sites.
  - `--query`, `-q <keywords>`: Match keywords in job title, team, or description.
  - `--board`, `-b <site>`: Target Lever site/board slug (e.g. `spotify`, `palantir`). Can be repeated or comma-separated. Default: standard tech target boards.
  - `--location`, `-l <location>`: Filter by location (e.g. `Pittsburgh`, `Remote`, `New York`).
  - `--jobage <days>`: Filter postings created within the last N days.
  - `--limit <n>`: Maximum number of results (default: 20).
  - `--format <json|table|plain>`: Output format (default: `json`).
- `detail <site> <posting_id>`: Fetch full posting description and requirements.

## Examples

```bash
# Search Android roles at Spotify
bun run .agents/skills/lever-search/cli/src/cli.ts search -q "Android" -b spotify --format table

# Search across multiple Lever companies
bun run .agents/skills/lever-search/cli/src/cli.ts search -q "Staff" -b "spotify,palantir" --format table

# Fetch job details
bun run .agents/skills/lever-search/cli/src/cli.ts detail spotify <id> --format plain
```
