import { type SearchOpts, writeError } from "../helpers.js"

export async function runSearch(opts: SearchOpts) {
  try {
    const res = await fetch("https://remoteok.com/api", {
      headers: {
        "User-Agent": "Mozilla/5.0 (compatible; ai-job-tracker/1.0)"
      }
    })
    if (!res.ok) {
      writeError(`RemoteOK API returned HTTP ${res.status}`, "NETWORK_ERROR")
      process.exit(1)
    }

    const data: any = await res.json()
    // First element is legal disclaimer
    const postings: any[] = Array.isArray(data) ? data.slice(1) : []

    const queryLower = (opts.query || "").toLowerCase()
    const locLower = (opts.location || "").toLowerCase()

    const results: Array<{
      id: string
      title: string
      company: string
      location: string
      salary: string
      tags: string[]
      url: string
      applyUrl: string
    }> = []

    for (const post of postings) {
      const title = post.position || ""
      const company = post.company || ""
      const location = post.location || "Remote"
      const tags: string[] = post.tags || []
      const tagsStr = tags.join(" ").toLowerCase()

      if (queryLower) {
        const matchesQuery = title.toLowerCase().includes(queryLower) ||
          tagsStr.includes(queryLower) ||
          (post.description && post.description.toLowerCase().includes(queryLower))
        if (!matchesQuery) continue
      }

      if (locLower) {
        const matchesLoc = location.toLowerCase().includes(locLower) || locLower.includes("remote")
        if (!matchesLoc) continue
      }

      const salaryMin = post.salary_min ? `$${(post.salary_min / 1000).toFixed(0)}k` : ""
      const salaryMax = post.salary_max ? `$${(post.salary_max / 1000).toFixed(0)}k` : ""
      const salary = salaryMin && salaryMax ? `${salaryMin}-${salaryMax}` : (salaryMin || salaryMax || "")

      results.push({
        id: post.id || post.slug,
        title,
        company,
        location,
        salary,
        tags,
        url: post.url || `https://remoteok.com/l/${post.id}`,
        applyUrl: post.apply_url || post.url || ""
      })
    }

    const limited = results.slice(0, opts.limit || 20)

    if (opts.format === "table") {
      if (limited.length === 0) {
        process.stdout.write("No matching RemoteOK jobs found.\n")
        return
      }
      const rows = limited.map((j) => ({
        Company: j.company.slice(0, 20),
        Title: j.title.slice(0, 35),
        Salary: j.salary,
        Location: j.location.slice(0, 20),
        URL: j.url
      }))
      console.table(rows)
    } else if (opts.format === "plain") {
      for (const j of limited) {
        process.stdout.write(`${j.company} | ${j.title} | ${j.salary} | ${j.location} | ${j.url}\n`)
      }
    } else {
      process.stdout.write(JSON.stringify(limited, null, 2) + "\n")
    }
  } catch (err: any) {
    writeError(err.message, "SEARCH_FAILED")
    process.exit(1)
  }
}
