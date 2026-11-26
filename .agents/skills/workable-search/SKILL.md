---
name: workable-search
description: Search job postings across company career pages hosted on Workable.
version: 1.0.0
---

# workable-search

Searches public Workable job boards via the Workable widget endpoint (`https://apply.workable.com/api/v1/widget/accounts/{account}`).

## Commands

- `search`: Search job listings across one or more company Workable subdomains.
  - `--query`, `-q <keywords>`: Match keywords in title.
  - `--account`, `-a <subdomains>`: Target company subdomain(s), comma-separated.
  - `--limit <n>`: Maximum number of results (default: 20).
  - `--format <json|table|plain>`: Output format (default: `json`).
