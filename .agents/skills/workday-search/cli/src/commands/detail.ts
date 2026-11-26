import {
  decodeHtml,
  fetchWorkdayJobDetail,
  KNOWN_WORKDAY_TENANTS,
  writeError
} from "../helpers.js"

export interface DetailOpts {
  tenant: string
  externalPath: string
  format: "json" | "plain"
}

export async function runDetail(opts: DetailOpts): Promise<void> {
  const config = KNOWN_WORKDAY_TENANTS[opts.tenant.toLowerCase()]
  if (!config) {
    writeError(`Unknown Workday tenant: ${opts.tenant}`, "UNKNOWN_TENANT")
    process.exit(1)
  }

  const data = await fetchWorkdayJobDetail(config, opts.externalPath)
  if (!data || !data.jobPostingInfo) {
    writeError(`Job posting not found at ${opts.externalPath}`, "NOT_FOUND")
    process.exit(1)
  }

  const info = data.jobPostingInfo
  const plainDesc = info.jobDescription ? decodeHtml(info.jobDescription) : "No description provided."

  if (opts.format === "json") {
    process.stdout.write(
      JSON.stringify(
        {
          id: info.id || info.jobReqId,
          company: config.name,
          title: info.title,
          location: info.location || info.locationsText,
          date: info.postedOn,
          url: `https://${config.host}/en-US/${config.site}${opts.externalPath}`,
          description: plainDesc
        },
        null,
        2
      ) + "\n"
    )
    return
  }

  process.stdout.write(`Title: ${info.title}\n`)
  process.stdout.write(`Company: ${config.name}\n`)
  process.stdout.write(`Location: ${info.location || info.locationsText || "Unspecified"}\n`)
  process.stdout.write(`Posted: ${info.postedOn || "—"}\n`)
  process.stdout.write(`URL: https://${config.host}/en-US/${config.site}${opts.externalPath}\n\n`)
  process.stdout.write(`--- Description ---\n\n`)
  process.stdout.write(plainDesc + "\n")
}
