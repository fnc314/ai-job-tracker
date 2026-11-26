import { type SearchOpts, writeError } from "../helpers.js"

export async function runSearch(opts: SearchOpts) {
  try {
    // 1. Find the latest "Ask HN: Who is hiring?" story
    const storyRes = await fetch("https://hn.algolia.com/api/v1/search_by_date?tags=story,author_whoishiring&hitsPerPage=10")
    if (!storyRes.ok) {
      writeError(`Algolia HN story search returned HTTP ${storyRes.status}`, "NETWORK_ERROR")
      process.exit(1)
    }

    const storyData: any = await storyRes.json()
    const hiringStory = (storyData.hits || []).find((h: any) =>
      h.title && h.title.toLowerCase().includes("who is hiring") && !h.title.toLowerCase().includes("wants to be hired")
    )

    if (!hiringStory) {
      writeError("No active 'Who is hiring?' thread found on Hacker News", "NOT_FOUND")
      process.exit(1)
    }

    const storyId = hiringStory.objectID
    const query = opts.query || "Android"

    // 2. Search comments inside the hiring thread
    const commentRes = await fetch(`https://hn.algolia.com/api/v1/search?tags=comment,story_${storyId}&query=${encodeURIComponent(query)}&hitsPerPage=50`)
    if (!commentRes.ok) {
      writeError(`Algolia HN comment search returned HTTP ${commentRes.status}`, "NETWORK_ERROR")
      process.exit(1)
    }

    const commentData: any = await commentRes.json()
    const hits: any[] = commentData.hits || []

    const results: Array<{
      id: string
      author: string
      company: string
      role: string
      url: string
      snippet: string
      createdAt: string
    }> = []

    for (const hit of hits) {
      const rawText = hit.comment_text || ""
      const cleanText = rawText.replace(/<[^>]+>/g, " ").replace(/&amp;/g, "&").replace(/&#x2F;/g, "/").replace(/&quot;/g, '"')

      // Parse the first line (typical HN format: "Company | Role | Location | Remote | URL")
      const lines = cleanText.split("\n").map((l: string) => l.trim()).filter(Boolean)
      const headerLine = lines[0] || cleanText.slice(0, 80)
      const parts = headerLine.split("|").map((p: string) => p.trim())

      const company = parts[0] || hit.author || "HN Poster"
      const role = parts[1] || query

      results.push({
        id: String(hit.objectID),
        author: hit.author,
        company,
        role,
        url: `https://news.ycombinator.com/item?id=${hit.objectID}`,
        snippet: cleanText.slice(0, 200) + "...",
        createdAt: hit.created_at
      })
    }

    const limited = results.slice(0, opts.limit || 20)

    if (opts.format === "table") {
      if (limited.length === 0) {
        process.stdout.write(`No Hacker News hiring comments matching "${query}" in ${hiringStory.title}.\n`)
        return
      }
      process.stdout.write(`From ${hiringStory.title}:\n\n`)
      const rows = limited.map((r) => ({
        Company: r.company.slice(0, 25),
        Role: r.role.slice(0, 35),
        Author: r.author,
        URL: r.url
      }))
      console.table(rows)
    } else if (opts.format === "plain") {
      for (const r of limited) {
        process.stdout.write(`${r.company} | ${r.role} | ${r.author} | ${r.url}\n`)
      }
    } else {
      process.stdout.write(JSON.stringify(limited, null, 2) + "\n")
    }
  } catch (err: any) {
    writeError(err.message, "SEARCH_FAILED")
    process.exit(1)
  }
}
