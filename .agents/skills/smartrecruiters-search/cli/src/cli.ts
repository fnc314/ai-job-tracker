#!/usr/bin/env bun
import { runSearch } from "./commands/search.js"
import { runDetail } from "./commands/detail.js"
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
  process.stdout.write(`smartrecruiters-search-cli — search SmartRecruiters company job boards

USAGE:
  bun run src/cli.ts search [options]
  bun run src/cli.ts detail <company> <job_id> [--format json|plain]

OPTIONS for search:
  -q, --query <keywords>      Keywords in title (e.g. "Android", "Engineering")
  -c, --company <tokens>      Company token(s), comma-separated (e.g. "Square,smartrecruiters")
  -l, --location <city>       Location substring
  --limit <n>                 Max results (default: 20)
  --format <json|table|plain> Output format (default: json)
`)
  process.exit(0)
}

if (command === "search") {
  const flags = parseFlags(args.slice(1))
  const compRaw = flags.company || flags.c || ""
  const companies = typeof compRaw === "string" && compRaw.length > 0
    ? compRaw.split(",").map((s: string) => s.trim()).filter(Boolean)
    : []

  await runSearch({
    query: flags.query || flags.q || undefined,
    companies,
    location: flags.location || flags.l || undefined,
    limit: parseInt(flags.limit || "20", 10),
    format: flags.format || "json"
  })
} else if (command === "detail") {
  const flags = parseFlags(args.slice(1))
  const company = flags._[0]
  const jobId = flags._[1]

  if (!company || !jobId) {
    writeError("Both <company> and <job_id> are required for detail", "INVALID_ARGS")
    process.exit(1)
  }

  await runDetail(company, jobId, flags.format || "json")
} else {
  writeError(`Unknown command: ${command}`, "UNKNOWN_COMMAND")
  process.exit(1)
}
