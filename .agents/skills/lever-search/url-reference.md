# Lever Job Postings Public API Reference

## Base Endpoints

Lever provides a public, unauthenticated REST API for companies hosting their career boards on Lever.co.

- **Board Postings (All)**: `GET https://api.lever.co/v0/postings/{site}?mode=json`
- **Single Job Detail**: `GET https://api.lever.co/v0/postings/{site}/{posting_id}`

## Response Format (Array of Postings)

```json
[
  {
    "id": "2a55d6c9-c564-443a-be3e-a3851db5a93b",
    "text": "Account Executive - Commercial, Spotify Technology",
    "createdAt": 1792767640768,
    "hostedUrl": "https://jobs.lever.co/spotify/2a55d6c9-c564-443a-be3e-a3851db5a93b",
    "applyUrl": "https://jobs.lever.co/spotify/2a55d6c9-c564-443a-be3e-a3851db5a93b/apply",
    "workplaceType": "hybrid",
    "categories": {
      "commitment": "Permanent",
      "department": "Operations and Business Support",
      "location": "New York, NY",
      "team": "Platform",
      "allLocations": ["New York, NY"]
    },
    "descriptionPlain": "Job description text...",
    "additionalPlain": "Compensation and perks..."
  }
]
```

## Board Sites

The `{site}` is the company's identifier in `jobs.lever.co/{site}` (e.g. `spotify`, `palantir`, `netflix`, `affirm`, `twitch`, `datadog`, `figma`).
