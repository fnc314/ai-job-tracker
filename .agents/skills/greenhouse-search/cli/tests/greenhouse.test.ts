import { describe, expect, test } from "bun:test"
import { fetchBoardJobs, fetchJobDetail } from "../src/helpers.js"

describe("greenhouse-search", () => {
  test("fetchBoardJobs returns valid job list for known public board", async () => {
    const jobs = await fetchBoardJobs("duolingo", false)
    expect(Array.isArray(jobs)).toBe(true)
    expect(jobs.length).toBeGreaterThan(0)
    const first = jobs[0]
    expect(first.id).toBeDefined()
    expect(first.title).toBeDefined()
    expect(first.absolute_url).toContain("duolingo")
  })

  test("fetchJobDetail returns valid job description", async () => {
    const jobs = await fetchBoardJobs("duolingo", false)
    const testJob = jobs[0]
    const detail = await fetchJobDetail("duolingo", testJob.id)
    expect(detail).not.toBeNull()
    expect(detail?.title).toBe(testJob.title)
    expect(detail?.content).toBeDefined()
  })
})
