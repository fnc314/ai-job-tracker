import { type SearchOpts, writeError } from "../helpers.js"

const DEFAULT_PORTALS = [
  "careers-fedex.icims.com",
  "careers-usps.icims.com",
  "careers-capitalone.icims.com"
]

export async function runSearch(opts: SearchOpts) {
  const targetPortals = opts.portals.length > 0 ? opts.portals : DEFAULT_PORTALS
  const query = opts.query || "Android"
  const allJobs: Array<{
    id: string
    title: string
    company: string
    location: string
    url: string
  }> = []

  await Promise.all(
    targetPortals.map(async (portalHost) => {
      try {
        const url = `https://${portalHost}/jobs/search?ss=1&searchKeyword=${encodeURIComponent(query)}&in_iframe=1`
        const res = await fetch(url, {
          headers: {
            "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36"
          }
        })
        if (!res.ok) return
        const html = await res.text()

        // Match job links and titles: href="/jobs/{id}/{slug}/job..."
        const linkRegex = /href="(\/jobs\/(\d+)\/([^/"]+)\/job[^"]*)"/g
        let match
        const seenInPortal = new Set<string>()

        while ((match = linkRegex.exec(html)) !== null) {
          const path = match[1]
          const id = match[2]
          const slug = match[3]
          if (seenInPortal.has(id)) continue
          seenInPortal.add(id)

          const cleanTitle = slug.replace(/-/g, " ").replace(/\b\w/g, (c) => c.toUpperCase())
          const company = portalHost.replace("careers-", "").replace(".icims.com", "")

          allJobs.push({
            id,
            title: cleanTitle,
            company,
            location: "See Posting",
            url: `https://${portalHost}${path}`
          })
        }
      } catch {
        // Silently continue
      }
    })
  )

  const limited = allJobs.slice(0, opts.limit || 20)

  if (opts.format === "table") {
    if (limited.length === 0) {
      process.stdout.write(`No matching iCIMS jobs found for query: "${query}".\n`)
      return
    }
    const rows = limited.map((j) => ({
      Company: j.company,
      Title: j.title.slice(0, 40),
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
