export interface SearchOpts {
  query?: string
  companies: string[]
  limit?: number
  format?: "json" | "table" | "plain"
}

export function writeError(message: string, code = "ERROR") {
  process.stderr.write(JSON.stringify({ error: message, code }) + "\n")
}
