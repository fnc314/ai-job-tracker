#!/usr/bin/env bun
import { runSearch } from "./commands/search.js"
import { writeError } from "./helpers.js"

const args = process.argv.slice(2)
const command = args[0]

function parseFlags(argv: string[]) {
  const flags: Record<string, any> = { _: [] }
  for (let i = 0; i < argv.length; i++) {
    const a = argv[i]
    if (!a.startsWith("-")) {
      flags._.push(a)
      continue
    }
    const name = a.replace(/^-+/, "")
    const next = argv[i + 1]
    let value: any = true
    if (next !== undefined && !next.startsWith("-")) {
      value = next
      i++
    }
    flags[name] = value
  }
  return flags
}

if (!command || command === "--help" || command === "-h") {
  process.stdout.write(`icims-search-cli — search enterprise iCIMS career portals

USAGE:
  bun run src/cli.ts search [options]

OPTIONS for search:
  -q, --query <keywords>      Keywords in search (e.g. "Android", "Staff")
  -p, --portal <hosts>        Portal hostnames, comma-separated (e.g. "careers-fedex.icims.com")
  --limit <n>                 Max results (default: 20)
  --format <json|table|plain> Output format (default: json)
`)
  process.exit(0)
}

if (command === "search") {
  const flags = parseFlags(args.slice(1))
  const portalsRaw = flags.portal || flags.p || ""
  const portals = typeof portalsRaw === "string" && portalsRaw.length > 0
    ? portalsRaw.split(",").map((s: string) => s.trim()).filter(Boolean)
    : []

  await runSearch({
    query: flags.query || flags.q || undefined,
    portals,
    limit: parseInt(flags.limit || "20", 10),
    format: flags.format || "json"
  })
} else {
  writeError(`Unknown command: ${command}`, "UNKNOWN_COMMAND")
  process.exit(1)
}
