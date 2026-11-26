import { type SearchOpts } from "../helpers.js"

const DEFAULT_ACCOUNTS = [
  "the-fabulous",
  "form",
  "creative-chaos",
  "proximity-works"
]

export async function runSearch(opts: SearchOpts) {
  const accounts = opts.accounts.length > 0 ? opts.accounts : DEFAULT_ACCOUNTS
  const queryLower = (opts.query || "").toLowerCase()
  const allJobs: Array<{
    id: string
    title: string
    company: string
    location: string
    url: string
  }> = []

  await Promise.all(
    accounts.map(async (account) => {
      try {
        const url = `https://apply.workable.com/api/v1/widget/accounts/${encodeURIComponent(account)}?details=true`
        const res = await fetch(url, {
          headers: {
            "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"
          }
        })
        if (!res.ok) return
        const data: any = await res.json()
        const jobs: any[] = data.jobs || []

        for (const j of jobs) {
          const title = j.title || ""
          const location = j.city ? `${j.city}, ${j.country || ""}` : (j.country || "Unspecified")

          if (queryLower && !title.toLowerCase().includes(queryLower)) {
            continue
          }

          allJobs.push({
            id: j.shortcode || j.id || title,
            title,
            company: data.name || account,
            location,
            url: j.url || `https://apply.workable.com/${account}/j/${j.shortcode}`
          })
        }
      } catch {
        // Silently skip
      }
    })
  )

  const limited = allJobs.slice(0, opts.limit || 20)

  if (opts.format === "table") {
    if (limited.length === 0) {
      process.stdout.write(`No matching Workable jobs found.\n`)
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
