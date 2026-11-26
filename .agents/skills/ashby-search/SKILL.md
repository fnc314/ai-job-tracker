---
name: ashby-search
description: Search job postings across company career boards hosted on Ashby ATS (e.g. OpenAI, Ramp, Linear, Retool, Vercel, Cursor, Perplexity, Anthropic).
version: 1.0.0
---

# ashby-search

Searches public Ashby ATS job boards directly via the official unauthenticated Ashby Posting API (`https://api.ashbyhq.com/posting-api/job-board/{board}`).

## Commands

- `search`: Search job listings across one or more company board tokens.
  - `--query`, `-q <keywords>`: Match keywords in job title or department.
  - `--board`, `-b <tokens>`: Target Ashby board token (e.g. `openai`, `ramp`, `linear`). Comma-separated.
  - `--location`, `-l <location>`: Filter by location or "Remote".
  - `--limit <n>`: Maximum number of results (default: 20).
  - `--format <json|table|plain>`: Output format (default: `json`).
- `detail <board> <job_id>`: Fetch full posting description and requirements.
