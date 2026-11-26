export interface SearchOpts {
  query?: string
  location?: string
  limit?: number
  format?: "json" | "table" | "plain"
}

export function writeError(message: string, code = "ERROR") {
  process.stderr.write(JSON.stringify({ error: message, code }) + "\n")
}
