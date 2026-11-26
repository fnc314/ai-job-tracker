// Helpers and tenant configurations for Workday CXS API
export interface WorkdayTenantConfig {
  name: string
  host: string
  tenant: string
  site: string
}

export const KNOWN_WORKDAY_TENANTS: Record<string, WorkdayTenantConfig> = {
  nvidia: {
    name: "Nvidia",
    host: "nvidia.wd5.myworkdayjobs.com",
    tenant: "nvidia",
    site: "NVIDIAExternalCareerSite"
  },
  target: {
    name: "Target",
    host: "target.wd5.myworkdayjobs.com",
    tenant: "target",
    site: "targetcareers"
  },
  paypal: {
    name: "PayPal",
    host: "paypal.wd1.myworkdayjobs.com",
    tenant: "paypal",
    site: "custom_careers"
  },
  autodesk: {
    name: "Autodesk",
    host: "autodesk.wd1.myworkdayjobs.com",
    tenant: "autodesk",
    site: "Ext"
  }
}

export interface WorkdayJobPosting {
  title: string
  externalPath: string
  locationsText?: string
  postedOn?: string
  bulletFields?: string[]
}

export interface JobResult {
  id: string
  tenant: string
  company: string
  title: string
  location: string
  date: string | null
  url: string
  externalPath: string
}

export function writeError(error: string, code: string): void {
  process.stderr.write(JSON.stringify({ error, code }) + "\n")
}

export function decodeHtml(html: string): string {
  return html
    .replace(/&lt;/g, "<")
    .replace(/&gt;/g, ">")
    .replace(/&amp;/g, "&")
    .replace(/&quot;/g, '"')
    .replace(/&#39;/g, "'")
    .replace(/&nbsp;/g, " ")
    .replace(/<br\s*[\/]?>/gi, "\n")
    .replace(/<\/p>/gi, "\n\n")
    .replace(/<\/li>/gi, "\n")
    .replace(/<[^>]+>/g, "")
    .replace(/\n{3,}/g, "\n\n")
    .trim()
}

export async function fetchWorkdayJobs(
  config: WorkdayTenantConfig,
  query: string,
  limit: number = 20
): Promise<WorkdayJobPosting[]> {
  const url = `https://${config.host}/wday/cxs/${config.tenant}/${config.site}/jobs`
  const body = {
    appliedFacets: {},
    limit,
    offset: 0,
    searchText: query
  }

  const response = await fetch(url, {
    method: "POST",
    headers: {
      "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
      "Content-Type": "application/json",
      Accept: "application/json"
    },
    body: JSON.stringify(body),
    signal: AbortSignal.timeout(15000)
  })

  if (!response.ok) {
    if (response.status === 404) return []
    throw new Error(`Workday error for ${config.name}: ${response.status} ${response.statusText}`)
  }

  const json = (await response.json()) as { jobPostings?: WorkdayJobPosting[] }
  return json.jobPostings || []
}

export async function fetchWorkdayJobDetail(
  config: WorkdayTenantConfig,
  externalPath: string
): Promise<any | null> {
  const cleanPath = externalPath.startsWith("/") ? externalPath : `/${externalPath}`
  const url = `https://${config.host}/wday/cxs/${config.tenant}/${config.site}${cleanPath}`

  const response = await fetch(url, {
    headers: {
      "User-Agent": "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36",
      Accept: "application/json"
    },
    signal: AbortSignal.timeout(15000)
  })

  if (!response.ok) {
    if (response.status === 404) return null
    throw new Error(`Workday detail error: ${response.status} ${response.statusText}`)
  }

  return await response.json()
}
