import { writeError, type DetailOpts } from "../helpers.js"

export async function runDetail(board: string, jobId: string, opts: DetailOpts = {}) {
  const url = `https://api.ashbyhq.com/posting-api/job-board/${encodeURIComponent(board)}`
  const res = await fetch(url)
  if (!res.ok) {
    writeError(`Failed to fetch Ashby board ${board}: HTTP ${res.status}`, "NETWORK_ERROR")
    process.exit(1)
  }

  const data: any = await res.json()
  const jobs: any[] = data.jobs || []
  const job = jobs.find((j) => j.id === jobId)

  if (!job) {
    writeError(`Job ${jobId} not found on board ${board}`, "NOT_FOUND")
    process.exit(1)
  }

  const out = {
    id: job.id,
    title: job.title,
    company: board,
    location: job.location || (job.isRemote ? "Remote" : "Unspecified"),
    isRemote: !!job.isRemote,
    workplaceType: job.workplaceType || "",
    publishedAt: job.publishedAt,
    url: job.jobUrl,
    applyUrl: job.applyUrl,
    description: job.descriptionPlain || job.descriptionHtml || ""
  }

  if (opts.format === "plain") {
    process.stdout.write(`=== ${out.title} ===\nCompany: ${out.company}\nLocation: ${out.location}\nURL: ${out.url}\n\n${out.description}\n`)
  } else {
    process.stdout.write(JSON.stringify(out, null, 2) + "\n")
  }
}
