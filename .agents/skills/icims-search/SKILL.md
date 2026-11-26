---
name: icims-search
description: Search job postings across enterprise company career portals powered by iCIMS ATS (e.g. FedEx, healthcare, and financial institutions).
version: 1.0.0
---

# icims-search

Searches public enterprise iCIMS career portal pages via iframe search endpoints and extracts structured posting links and metadata.

## Commands

- `search`: Search job listings across one or more company iCIMS portals.
  - `--query`, `-q <keywords>`: Match keywords in job title or description.
  - `--portal`, `-p <hosts>`: Target iCIMS portal hostname(s), comma-separated.
  - `--limit <n>`: Maximum number of results (default: 20).
  - `--format <json|table|plain>`: Output format (default: `json`).
