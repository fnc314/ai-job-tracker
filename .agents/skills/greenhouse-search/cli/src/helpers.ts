// Helpers for greenhouse-search CLI
export interface GreenhouseJob {
  id: number
  internal_job_id?: number
  title: string
  company_name?: string
  location?: { name?: string }
  absolute_url: string
  updated_at?: string
  first_published?: string
  content?: string
}

export interface JobResult {
  id: string
  board: string
  title: string
  company: string
  location: string
  date: string | null
  url: string
  description?: string
}

export const DEFAULT_BOARDS = [
  "duolingo",
  "figma",
  "reddit",
  "gusto",
  "robinhood",
  "datadog",
  "stripe",
  "pinterest",
  "instacart",
  "dropbox"
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

export async function fetchBoardJobs(board: string, includeContent: boolean = false): Promise<GreenhouseJob[]> {
  const url = `https://boards-api.greenhouse.io/v1/boards/${encodeURIComponent(board)}/jobs${includeContent ? "?content=true" : ""}`
  const response = await fetch(url, {
    headers: {
      "User-Agent": "greenhouse-search-skill/1.0",
      Accept: "application/json"
    },
    signal: AbortSignal.timeout(15000)
  })

  if (!response.ok) {
    if (response.status === 404) return []
    throw new Error(`Failed to fetch board ${board}: ${response.status} ${response.statusText}`)
  }

  const json = (await response.json()) as { jobs?: GreenhouseJob[] }
  return json.jobs || []
}

export async function fetchJobDetail(board: string, jobId: string | number): Promise<GreenhouseJob | null> {
  const url = `https://boards-api.greenhouse.io/v1/boards/${encodeURIComponent(board)}/jobs/${jobId}`
  const response = await fetch(url, {
    headers: {
      "User-Agent": "greenhouse-search-skill/1.0",
      Accept: "application/json"
    },
    signal: AbortSignal.timeout(15000)
  })

  if (!response.ok) {
    if (response.status === 404) return null
    throw new Error(`Failed to fetch job ${jobId} on board ${board}: ${response.status} ${response.statusText}`)
  }

  return (await response.json()) as GreenhouseJob
}
