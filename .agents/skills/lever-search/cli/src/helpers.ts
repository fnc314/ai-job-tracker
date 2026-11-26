// Helpers for lever-search CLI
export interface LeverPosting {
  id: string
  text: string
  createdAt?: number
  hostedUrl: string
  applyUrl?: string
  workplaceType?: string
  categories?: {
    commitment?: string
    department?: string
    location?: string
    team?: string
    allLocations?: string[]
  }
  description?: string
  descriptionPlain?: string
  additional?: string
  additionalPlain?: string
}

export interface JobResult {
  id: string
  board: string
  title: string
  company: string
  location: string
  date: string | null
  url: string
}

export const DEFAULT_LEVER_BOARDS = [
  "spotify",
  "palantir",
  "affirm",
  "twitch",
  "plaid",
  "benchling",
  "eventbrite"
]

export function writeError(error: string, code: string): void {
  process.stderr.write(JSON.stringify({ error, code }) + "\n")
}

export function decodeHtml(html: string): string {
  return html
    .replace(/&lt;/g, "<")
    .replace(/&gt;/g, ">")
    .replace(/&amp;/g, "&")
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/&nbsp;/g, " ")
    .replace(/<br\s*[\/]?>/gi, "\n")
    .replace(/<\/p>/gi, "\n\n")
    .replace(/<\/li>/gi, "\n")
    .replace(/<[^>]+>/g, "")
    .replace(/\n{3,}/g, "\n\n")
    .trim()
}

export async function fetchLeverPostings(site: string): Promise<LeverPosting[]> {
  const url = `https://api.lever.co/v0/postings/${encodeURIComponent(site)}?mode=json`
  const response = await fetch(url, {
    headers: {
      "User-Agent": "lever-search-skill/1.0",
      Accept: "application/json"
    },
    signal: AbortSignal.timeout(15000)
  })

  if (!response.ok) {
    if (response.status === 404) return []
    throw new Error(`Failed to fetch Lever board ${site}: ${response.status} ${response.statusText}`)
  }

  const json = (await response.json()) as any
  if (Array.isArray(json)) return json as LeverPosting[]
  return []
}

export async function fetchLeverPostingDetail(site: string, postingId: string): Promise<LeverPosting | null> {
  const url = `https://api.lever.co/v0/postings/${encodeURIComponent(site)}/${postingId}`
  const response = await fetch(url, {
    headers: {
      "User-Agent": "lever-search-skill/1.0",
      Accept: "application/json"
    },
    signal: AbortSignal.timeout(15000)
  })

  if (!response.ok) {
    if (response.status === 404) return null
    throw new Error(`Failed to fetch Lever posting ${postingId} on board ${site}: ${response.status} ${response.statusText}`)
  }

  return (await response.json()) as LeverPosting
}
