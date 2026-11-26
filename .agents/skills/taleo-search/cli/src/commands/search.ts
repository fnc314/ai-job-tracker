import { type SearchOpts, writeError } from "../helpers.js"

const DEFAULT_COMPANIES = [
  "pnc",
  "deloitte",
  "ge"
]

export async function runSearch(opts: SearchOpts) {
  const companies = opts.companies.length > 0 ? opts.companies : DEFAULT_COMPANIES
  const query = opts.query || "Android"
  const allJobs: Array<{
    id: string
    title: string
    company: string
    url: string
  }> = []

  await Promise.all(
    companies.map(async (company) => {
      try {
        const url = `https://${encodeURIComponent(company)}.taleo.net/careersection/2/jobsearch.ftl?lang=en`
        const res = await fetch(url, {
          headers: {
            "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"
          }
        })
        if (!res.ok) return
        const html = await res.text()

        // Match job links: jobdetail.ftl?job={id}
        const regex = /jobdetail\.ftl\?job=([^"&'\s]+)/g
        let match
        const seen = new Set<string>()

        while ((match = regex.exec(html)) !== null) {
          const id = match[1]
          if (seen.has(id)) continue
          seen.add(id)

          allJobs.push({
            id,
            title: `Position Requisition #${id}`,
            company,
            url: `https://${company}.taleo.net/careersection/2/jobdetail.ftl?job=${id}`
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
      process.stdout.write(`No matching Taleo jobs found for query: "${query}".\n`)
      return
    }
    const rows = limited.map((j) => ({
      Company: j.company,
      Title: j.title,
      URL: j.url
    }))
    console.table(rows)
  } else if (opts.format === "plain") {
    for (const j of limited) {
      process.stdout.write(`${j.company} | ${j.title} | ${j.url}\n`)
    }
  } else {
    process.stdout.write(JSON.stringify(limited, null, 2) + "\n")
  }
}
