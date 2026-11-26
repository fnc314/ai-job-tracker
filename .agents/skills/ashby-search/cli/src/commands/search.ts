import { type SearchOpts } from "../helpers.js"

const DEFAULT_BOARDS = [
  "openai",
  "ramp",
  "linear",
  "retool",
  "vercel",
  "cursor",
  "perplexity",
  "anthropic",
  "remote"
]

export async function runSearch(opts: SearchOpts) {
  const targetBoards = opts.boards.length > 0 ? opts.boards : DEFAULT_BOARDS
  const queryLower = (opts.query || "").toLowerCase()
  const locLower = (opts.location || "").toLowerCase()

  const allJobs: Array<{
    id: string
    title: string
    company: string
    location: string
    isRemote: boolean
    publishedAt: string
    url: string
  }> = []

  await Promise.all(
    targetBoards.map(async (board) => {
      try {
        const url = `https://api.ashbyhq.com/posting-api/job-board/${encodeURIComponent(board)}`
        const res = await fetch(url)
        if (!res.ok) return
        const data: any = await res.json()
        const jobs: any[] = data.jobs || []

        for (const job of jobs) {
          const title = job.title || ""
          const location = job.location || (job.isRemote ? "Remote" : "Unspecified")

          if (queryLower) {
            const matchesQuery = title.toLowerCase().includes(queryLower) ||
              (job.department && job.department.toLowerCase().includes(queryLower)) ||
              (job.team && job.team.toLowerCase().includes(queryLower))
            if (!matchesQuery) continue
          }

          if (locLower) {
            const matchesLoc = location.toLowerCase().includes(locLower) ||
              (locLower.includes("remote") && job.isRemote)
            if (!matchesLoc) continue
          }

          allJobs.push({
            id: job.id,
            title,
            company: board,
            location,
            isRemote: !!job.isRemote,
            publishedAt: job.publishedAt || "",
            url: job.jobUrl
          })
        }
      } catch {
        // Silently skip unavailable boards
      }
    })
  )

  const limited = allJobs.slice(0, opts.limit || 20)

  if (opts.format === "table") {
    if (limited.length === 0) {
      process.stdout.write("No matching Ashby jobs found.\n")
      return
    }
    const rows = limited.map((j) => ({
      Company: j.company,
      Title: j.title.slice(0, 40),
      Location: j.location.slice(0, 25),
      URL: j.url
    }))
    console.table(rows)
  } else if (opts.format === "plain") {
    for (const j of limited) {
      process.stdout.write(`${j.company} | ${j.title} | ${j.location} | ${j.url}\n`)
    }
  } else {
    process.stdout.write(JSON.stringify(limited, null, 2) + "\n")
  }
}
