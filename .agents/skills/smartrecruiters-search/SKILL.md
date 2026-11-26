---
name: smartrecruiters-search
description: Search job postings across company career boards hosted on SmartRecruiters (e.g. Square/Block, SmartRecruiters, Visa, Bosch).
version: 1.0.0
---

# smartrecruiters-search

Searches public SmartRecruiters company boards directly via the official SmartRecruiters Public Postings API.

## Commands

- `search`: Search job listings across one or more company identifiers.
  - `--query`, `-q <keywords>`: Match keywords in job title.
  - `--company`, `-c <tokens>`: Target company identifier(s), comma-separated.
  - `--location`, `-l <location>`: Filter by location or remote.
  - `--limit <n>`: Maximum number of results (default: 20).
  - `--format <json|table|plain>`: Output format (default: `json`).
- `detail <company> <job_id>`: Fetch full posting description and requirements.
