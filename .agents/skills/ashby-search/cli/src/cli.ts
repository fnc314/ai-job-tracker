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
  process.stdout.write(`ashby-search-cli — search company Ashby ATS job boards

USAGE:
  bun run src/cli.ts search [options]
  bun run src/cli.ts detail <board> <job_id> [--format json|plain]

OPTIONS for search:
  -q, --query <keywords>      Keywords in title/department (e.g. "Android", "Staff")
  -b, --board <tokens>        Board token(s), comma-separated (e.g. "openai,ramp")
  -l, --location <city>       Location substring (e.g. "Remote", "New York")
  --limit <n>                 Max results (default: 20)
  --format <json|table|plain> Output format (default: json)
`)
  process.exit(0)
}

if (command === "search") {
  const flags = parseFlags(args.slice(1))
  const boardsRaw = flags.board || flags.b || ""
  const boards = typeof boardsRaw === "string" && boardsRaw.length > 0
    ? boardsRaw.split(",").map((s: string) => s.trim()).filter(Boolean)
    : []

  await runSearch({
    query: flags.query || flags.q || undefined,
    boards,
    location: flags.location || flags.l || undefined,
    limit: parseInt(flags.limit || "20", 10),
    format: flags.format || "json"
  })
} else if (command === "detail") {
  const flags = parseFlags(args.slice(1))
  const board = flags._[0]
  const jobId = flags._[1]

  if (!board || !jobId) {
    writeError("Both <board> and <job_id> are required for detail", "INVALID_ARGS")
    process.exit(1)
  }

  await runDetail(board, jobId, {
    format: flags.format || "json"
  })
} else {
  writeError(`Unknown command: ${command}`, "UNKNOWN_COMMAND")
  process.exit(1)
}
