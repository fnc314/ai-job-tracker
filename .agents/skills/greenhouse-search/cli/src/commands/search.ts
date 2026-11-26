import {
  DEFAULT_BOARDS,
  decodeHtml,
  fetchBoardJobs,
  type JobResult
} from "../helpers.js"

export interface SearchOpts {
  query?: string
  boards: string[]
  location?: string
  jobage: number
  page: number
  limit: number
  format: "json" | "table" | "plain"
}

export async function runSearch(opts: SearchOpts): Promise<void> {
  const boards = opts.boards.length > 0 ? opts.boards : DEFAULT_BOARDS
  const queryTerms = opts.query ? opts.query.toLowerCase().split(/\s+/).filter(Boolean) : []
  const locationTerm = opts.location ? opts.location.toLowerCase() : null
  const now = Date.now()
  const maxAgeMs = opts.jobage > 0 ? opts.jobage * 24 * 60 * 60 * 1000 : Infinity

  const results: JobResult[] = []

  await Promise.all(
    boards.map(async (board) => {
      try {
        const jobs = await fetchBoardJobs(board, false)
        for (const job of jobs) {
          const title = job.title || ""
          const loc = job.location?.name || ""
          const company = job.company_name || board.charAt(0).toUpperCase() + board.slice(1)
          const updatedStr = job.updated_at || job.first_published || null
          const updatedTime = updatedStr ? new Date(updatedStr).getTime() : 0

          if (opts.jobage > 0 && updatedTime > 0 && now - updatedTime > maxAgeMs) {
            continue
          }

          if (locationTerm && !loc.toLowerCase().includes(locationTerm)) {
            continue
          }

          if (queryTerms.length > 0) {
            const matchesQuery = queryTerms.every((term) =>
              title.toLowerCase().includes(term) || loc.toLowerCase().includes(term)
            )
            if (!matchesQuery) continue
          }

          results.push({
            id: String(job.id),
            board,
            title,
            company,
            location: loc || "Remote / Unspecified",
            date: updatedStr ? updatedStr.slice(0, 10) : null,
            url: job.absolute_url
          })
        }
      } catch {
        // Ignore single failing board and continue
      }
    })
  )

  // Sort newest first
  results.sort((a, b) => ((b.date || "") > (a.date || "") ? 1 : -1))

  const paginated = results.slice((opts.page - 1) * opts.limit, opts.page * opts.limit)

  if (opts.format === "json") {
    process.stdout.write(
      JSON.stringify(
        {
          meta: {
            total: results.length,
            count: paginated.length,
            page: opts.page,
            boards_searched: boards.length
          },
          results: paginated
        },
        null,
        2
      ) + "\n"
    )
    return
  }

  if (opts.format === "plain") {
    for (const r of paginated) {
      process.stdout.write(`${r.title} | ${r.company} | ${r.location} | ${r.url}\n`)
    }
    return
  }

  // Table format
  if (paginated.length === 0) {
    process.stdout.write("No matching postings found.\n")
    return
  }

  const pad = (s: string, len: number) => (s.length > len ? s.slice(0, len - 3) + "..." : s.padEnd(len))
  process.stdout.write(
    `${pad("COMPANY", 15)} ${pad("TITLE", 35)} ${pad("LOCATION", 22)} ${pad("DATE", 12)} URL\n`
  )
  process.stdout.write(`${"-".repeat(15)} ${"-".repeat(35)} ${"-".repeat(22)} ${"-".repeat(12)} ${"-".repeat(30)}\n`)
  for (const r of paginated) {
    process.stdout.write(
      `${pad(r.company, 15)} ${pad(r.title, 35)} ${pad(r.location, 22)} ${pad(r.date || "—", 12)} ${r.url}\n`
    )
  }
}
