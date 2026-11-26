# Greenhouse Job Board Public API Reference

## Base Endpoints

Greenhouse provides a public, unauthenticated REST API for companies hosting their career boards on Greenhouse.

- **Board Jobs Summary**: `GET https://boards-api.greenhouse.io/v1/boards/{board_token}/jobs`
- **Board Jobs with Content**: `GET https://boards-api.greenhouse.io/v1/boards/{board_token}/jobs?content=true`
- **Single Job Detail**: `GET https://boards-api.greenhouse.io/v1/boards/{board_token}/jobs/{job_id}`

## Response Format

```json
{
  "jobs": [
    {
      "id": 8851677002,
      "internal_job_id": 6543424002,
      "title": "Ad Sales Lead, Programmatic",
      "company_name": "Duolingo",
      "location": { "name": "New York, NY" },
      "absolute_url": "https://careers.duolingo.com/jobs/8851677002?gh_jid=8851677002",
      "updated_at": "2026-11-02T11:38:30-05:00",
      "first_published": "2026-09-28T09:09:53-04:00",
      "content": "&lt;p&gt;Job description HTML...&lt;/p&gt;"
    }
  ]
}
```

## Board Tokens

The `{board_token}` is the slug of the company in their Greenhouse board URL (e.g. `duolingo`, `figma`, `airbnb`, `stripe`, `gusto`, `robinhood`, `datadog`, `reddit`).
Multiple boards can be searched concurrently or configured as a target company list.
