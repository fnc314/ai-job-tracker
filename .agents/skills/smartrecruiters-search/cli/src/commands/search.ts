import { type SearchOpts } from "../helpers.js"

const DEFAULT_COMPANIES = [
  "Square",
  "smartrecruiters",
  "visa",
  "BoschGroup",
  "Skechers"
]

export async function runSearch(opts: SearchOpts) {
  const targetCompanies = opts.companies.length > 0 ? opts.companies : DEFAULT_COMPANIES
  const queryLower = (opts.query || "").toLowerCase()
  const locLower = (opts.location || "").toLowerCase()

  const allJobs: Array<{
    id: string
    title: string
    company: string
    location: string
    url: string
  }> = []

  await Promise.all(
    targetCompanies.map(async (company) => {
      try {
        const queryParam = opts.query ? `&q=${encodeURIComponent(opts.query)}` : ""
        const url = `https://api.smartrecruiters.com/v1/companies/${encodeURIComponent(company)}/postings?limit=50${queryParam}`
        const res = await fetch(url)
        if (!res.ok) return
        const data: any = await res.json()
        const postings: any[] = data.content || []

        for (const item of postings) {
          const title = item.name || ""
          const location = item.location?.fullLocation || item.location?.city || "Unspecified"
          const isRemote = !!item.location?.remote

          if (queryLower && !title.toLowerCase().includes(queryLower)) {
            continue
          }

          if (locLower) {
            const matchesLoc = location.toLowerCase().includes(locLower) || (locLower.includes("remote") && isRemote)
            if (!matchesLoc) continue
          }

          allJobs.push({
            id: item.id,
            title,
            company,
            location: isRemote ? `${location} (Remote)` : location,
            url: `https://jobs.smartrecruiters.com/${encodeURIComponent(company)}/${item.id}`
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
      process.stdout.write("No matching SmartRecruiters jobs found.\n")
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
