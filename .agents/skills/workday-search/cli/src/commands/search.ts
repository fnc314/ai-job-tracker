import {
  KNOWN_WORKDAY_TENANTS,
  fetchWorkdayJobs,
  type JobResult,
  type WorkdayTenantConfig
} from "../helpers.js"

export interface SearchOpts {
  query?: string
  tenants: string[]
  location?: string
  page: number
  limit: number
  format: "json" | "table" | "plain"
}

export async function runSearch(opts: SearchOpts): Promise<void> {
  const targetTenants: WorkdayTenantConfig[] = []

  if (opts.tenants.length > 0) {
    for (const key of opts.tenants) {
      const cfg = KNOWN_WORKDAY_TENANTS[key.toLowerCase()]
      if (cfg) targetTenants.push(cfg)
    }
  } else {
    targetTenants.push(...Object.values(KNOWN_WORKDAY_TENANTS))
  }

  const locationTerm = opts.location ? opts.location.toLowerCase() : null
  const query = opts.query || ""
  const results: JobResult[] = []

  await Promise.all(
    targetTenants.map(async (config) => {
      try {
        const postings = await fetchWorkdayJobs(config, query, opts.limit)
        for (const post of postings) {
          const loc = post.locationsText || "Unspecified / Remote"
          if (locationTerm && !loc.toLowerCase().includes(locationTerm)) {
            continue
          }

          const fullUrl = `https://${config.host}/en-US/${config.site}${post.externalPath}`
          const id = post.bulletFields?.[0] || post.externalPath.split("_").pop() || post.externalPath

          results.push({
            id,
            tenant: config.tenant,
            company: config.name,
            title: post.title,
            location: loc,
            date: post.postedOn || null,
            url: fullUrl,
            externalPath: post.externalPath
          })
        }
      } catch {
        // Skip failing tenant gracefully
      }
    })
  )

  const paginated = results.slice((opts.page - 1) * opts.limit, opts.page * opts.limit)

  if (opts.format === "json") {
    process.stdout.write(
      JSON.stringify(
        {
          meta: {
            total: results.length,
            count: paginated.length,
            page: opts.page,
            tenants_searched: targetTenants.length
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

  if (paginated.length === 0) {
    process.stdout.write("No matching postings found.\n")
    return
  }

  const pad = (s: string, len: number) => (s.length > len ? s.slice(0, len - 3) + "..." : s.padEnd(len))
  process.stdout.write(
    `${pad("COMPANY", 12)} ${pad("TITLE", 38)} ${pad("LOCATION", 22)} ${pad("POSTED", 15)} URL\n`
  )
  process.stdout.write(`${"-".repeat(12)} ${"-".repeat(38)} ${"-".repeat(22)} ${"-".repeat(15)} ${"-".repeat(30)}\n`)
  for (const r of paginated) {
    process.stdout.write(
      `${pad(r.company, 12)} ${pad(r.title, 38)} ${pad(r.location, 22)} ${pad(r.date || "—", 15)} ${r.url}\n`
    )
  }
}
