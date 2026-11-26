import { decodeHtml, fetchJobDetail, writeError } from "../helpers.js"

export interface DetailOpts {
  board: string
  jobId: string
  format: "json" | "plain"
}

export async function runDetail(opts: DetailOpts): Promise<void> {
  const job = await fetchJobDetail(opts.board, opts.jobId)
  if (!job) {
    writeError(`Job ${opts.jobId} not found on board ${opts.board}`, "NOT_FOUND")
    process.exit(1)
  }

  const plainContent = job.content ? decodeHtml(job.content) : "No description provided."

  if (opts.format === "json") {
    process.stdout.write(
      JSON.stringify(
        {
          id: String(job.id),
          board: opts.board,
          title: job.title,
          company: job.company_name || opts.board,
          location: job.location?.name || "Unspecified",
          url: job.absolute_url,
          date: (job.updated_at || job.first_published || "").slice(0, 10) || null,
          content: plainContent
        },
        null,
        2
      ) + "\n"
    )
    return
  }

  process.stdout.write(`Title: ${job.title}\n`)
  process.stdout.write(`Company: ${job.company_name || opts.board}\n`)
  process.stdout.write(`Location: ${job.location?.name || "Unspecified"}\n`)
  process.stdout.write(`URL: ${job.absolute_url}\n`)
  process.stdout.write(`Date: ${(job.updated_at || job.first_published || "").slice(0, 10) || "—"}\n\n`)
  process.stdout.write(`--- Description ---\n\n`)
  process.stdout.write(plainContent + "\n")
}
