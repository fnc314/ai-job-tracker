#!/usr/bin/env bun
import { runSearch, type SearchOpts } from "./commands/search.js"
import { runDetail, type DetailOpts } from "./commands/detail.js"
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
  process.stdout.write(`lever-cli — search company Lever ATS job boards

USAGE:
  bun run src/cli.ts search [options]
  bun run src/cli.ts detail <board> <posting_id> [--format json|plain]

OPTIONS for search:
  -q, --query <keywords>      Keywords in title/team/location (e.g. "Android", "Staff")
  -b, --board <sites>         Lever site slug(s), comma-separated (e.g. "spotify,palantir")
  -l, --location <city>       Location substring (e.g. "Pittsburgh", "Remote", "New York")
  --jobage <days>             Created within N days
  --limit <n>                 Max results (default: 20)
  --page <n>                  Page number (default: 1)
  --format <json|table|plain> Output format (default: json)
`)
  process.exit(0)
}

if (command === "search") {
  const flags = parseFlags(args.slice(1))
  const boardsRaw = flags.board || flags.b || ""
  const boards = typeof boardsRaw === "string" && boardsRaw.length > 0
    ? boardsRaw.split(",").map((s) => s.trim()).filter(Boolean)
    : []

  const opts: SearchOpts = {
    query: flags.query || flags.q || undefined,
    boards,
    location: flags.location || flags.l || undefined,
    jobage: parseInt(flags.jobage || "0", 10),
    page: parseInt(flags.page || "1", 10),
    limit: parseInt(flags.limit || "20", 10),
    format: flags.format || "json"
  }

  try {
    await runSearch(opts)
  } catch (err: any) {
    writeError(err.message, "SEARCH_FAILED")
    process.exit(1)
  }
} else if (command === "detail") {
  const flags = parseFlags(args.slice(1))
  const board = flags._[0]
  const postingId = flags._[1]

  if (!board || !postingId) {
    writeError("Both <board> and <posting_id> are required for detail", "INVALID_ARGS")
    process.exit(1)
  }

  const opts: DetailOpts = {
    board,
    postingId,
    format: flags.format || "plain"
  }

  try {
    await runDetail(opts)
  } catch (err: any) {
    writeError(err.message, "DETAIL_FAILED")
    process.exit(1)
  }
} else {
  writeError(`Unknown command: ${command}`, "UNKNOWN_COMMAND")
  process.exit(1)
}
