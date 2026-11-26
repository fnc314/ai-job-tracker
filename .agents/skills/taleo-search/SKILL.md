---
name: taleo-search
description: Search job postings across legacy enterprise company career portals hosted on Oracle Taleo (e.g. PNC, Deloitte, GE).
version: 1.0.0
---

# taleo-search

Searches public Oracle Taleo enterprise career sections (`*.taleo.net/careersection/2/jobsearch.ftl`).

## Commands

- `search`: Search job listings across one or more company Taleo portals.
  - `--query`, `-q <keywords>`: Match keywords.
  - `--company`, `-c <tokens>`: Target company Taleo subdomain(s), comma-separated.
  - `--limit <n>`: Maximum number of results (default: 20).
  - `--format <json|table|plain>`: Output format (default: `json`).
