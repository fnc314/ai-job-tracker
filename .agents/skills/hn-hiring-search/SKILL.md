---
name: hn-hiring-search
description: Search monthly Hacker News "Who is hiring?" threads for direct engineering leads and roles without ATS gatekeeping.
version: 1.0.0
---

# hn-hiring-search

Queries the official Algolia Hacker News Search API for comments inside the latest monthly "Ask HN: Who is hiring?" thread.

## Commands

- `search`: Search comments in the current monthly hiring thread.
  - `--query`, `-q <keywords>`: Match keywords in comment text (e.g. `Android`, `Staff`, `Principal`).
  - `--limit <n>`: Maximum number of comments to return (default: 20).
  - `--format <json|table|plain>`: Output format (default: `json`).
