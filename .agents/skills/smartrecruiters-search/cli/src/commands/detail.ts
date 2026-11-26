import { writeError } from "../helpers.js"

export async function runDetail(company: string, jobId: string, format = "json") {
  const url = `https://api.smartrecruiters.com/v1/companies/${encodeURIComponent(company)}/postings/${encodeURIComponent(jobId)}`
  const res = await fetch(url)
  if (!res.ok) {
    writeError(`Failed to fetch SmartRecruiters posting: HTTP ${res.status}`, "NETWORK_ERROR")
    process.exit(1)
  }

  const data: any = await res.json()
  const out = {
    id: data.id,
    title: data.name,
    company: company,
    location: data.location?.fullLocation || data.location?.city || "Unspecified",
    releasedDate: data.releasedDate,
    url: `https://jobs.smartrecruiters.com/${encodeURIComponent(company)}/${data.id}`,
    description: data.jobAd?.sections?.jobDescription?.text || ""
  }

  if (format === "plain") {
    process.stdout.write(`=== ${out.title} ===\nCompany: ${out.company}\nLocation: ${out.location}\nURL: ${out.url}\n\n${out.description}\n`)
  } else {
    process.stdout.write(JSON.stringify(out, null, 2) + "\n")
  }
}
