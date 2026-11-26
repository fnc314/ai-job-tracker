---
name: workday-search
description: Search job postings across company career sites powered by Workday CXS (e.g., Nvidia, Target, Adobe, Autodesk, PayPal, etc.). Use to find engineering, Android, mobile, or tech jobs directly at enterprise firms.
version: 1.0.0
---

# workday-search

Searches enterprise Workday candidate portals directly via the Workday CXS JSON API.

## Commands

- `search`: Search job listings across configured Workday tenants.
  - `--query`, `-q <keywords>`: Match keywords (e.g. `Android`, `Staff`, `Principal`).
  - `--tenant`, `-t <name>`: Target tenant key (e.g. `nvidia`, `target`).
  - `--location`, `-l <location>`: Filter results by location text.
  - `--limit <n>`: Maximum number of results (default: 20).
  - `--format <json|table|plain>`: Output format (default: `json`).
- `detail <tenant> <externalPath>`: Fetch full posting description and requirements.

## Examples

```bash
# Search Android roles at Nvidia
bun run .agents/skills/workday-search/cli/src/cli.ts search -q "Android" -t nvidia --format table

# Search Android roles at Target
bun run .agents/skills/workday-search/cli/src/cli.ts search -q "Android" -t target --format table

# Search all configured Workday tenants
bun run .agents/skills/workday-search/cli/src/cli.ts search -q "Android" --format table
```
