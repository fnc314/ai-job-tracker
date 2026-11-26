---
name: remoteok-search
description: Search remote software engineering and tech job vacancies across RemoteOK with transparent salary ranges.
version: 1.0.0
---

# remoteok-search

Searches global remote tech postings via the public RemoteOK API feed.

## Commands

- `search`: Search listings matching keywords, tags, or locations.
  - `--query`, `-q <keywords>`: Match keywords in title, tags, or description.
  - `--location`, `-l <location>`: Match location filter or country.
  - `--limit <n>`: Maximum number of results (default: 20).
  - `--format <json|table|plain>`: Output format (default: `json`).
