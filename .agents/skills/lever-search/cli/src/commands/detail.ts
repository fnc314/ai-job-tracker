import { decodeHtml, fetchLeverPostingDetail, writeError } from "../helpers.js"

export interface DetailOpts {
  board: string
  postingId: string
  format: "json" | "plain"
}

export async function runDetail(opts: DetailOpts): Promise<void> {
  const post = await fetchLeverPostingDetail(opts.board, opts.postingId)
  if (!post) {
    writeError(`Posting ${opts.postingId} not found on Lever board ${opts.board}`, "NOT_FOUND")
    process.exit(1)
  }

  const rawDesc = post.descriptionPlain || (post.description ? decodeHtml(post.description) : "")
  const rawAdd = post.additionalPlain || (post.additional ? decodeHtml(post.additional) : "")
  const plainContent = (rawDesc || "No description provided.") + (rawAdd ? `\n\n--- Additional Info ---\n${rawAdd}` : "")

  if (opts.format === "json") {
    process.stdout.write(
      JSON.stringify(
        {
          id: post.id,
          board: opts.board,
          title: post.text,
          company: opts.board.charAt(0).toUpperCase() + opts.board.slice(1),
          location: post.categories?.location || post.workplaceType || "Unspecified",
          url: post.hostedUrl,
          applyUrl: post.applyUrl,
          date: post.createdAt ? new Date(post.createdAt).toISOString().slice(0, 10) : null,
          content: plainContent
        },
        null,
        2
      ) + "\n"
    )
    return
  }

  process.stdout.write(`Title: ${post.text}\n`)
  process.stdout.write(`Company: ${opts.board.charAt(0).toUpperCase() + opts.board.slice(1)}\n`)
  process.stdout.write(`Location: ${post.categories?.location || post.workplaceType || "Unspecified"}\n`)
  process.stdout.write(`URL: ${post.hostedUrl}\n`)
  process.stdout.write(`Date: ${post.createdAt ? new Date(post.createdAt).toISOString().slice(0, 10) : "—"}\n\n`)
  process.stdout.write(`--- Description ---\n\n`)
  process.stdout.write(plainContent + "\n")
}
