import { describe, expect, test } from "bun:test"
import { fetchWorkdayJobs, fetchWorkdayJobDetail, KNOWN_WORKDAY_TENANTS } from "../src/helpers.js"

describe("workday-search", () => {
  test("fetchWorkdayJobs returns valid jobs for known Workday tenant", async () => {
    const config = KNOWN_WORKDAY_TENANTS["target"]
    const jobs = await fetchWorkdayJobs(config, "Android", 5)
    expect(Array.isArray(jobs)).toBe(true)
    expect(jobs.length).toBeGreaterThan(0)
    const first = jobs[0]
    expect(first.title).toBeDefined()
    expect(first.externalPath).toBeDefined()
  })

  test("fetchWorkdayJobDetail returns valid job description", async () => {
    const config = KNOWN_WORKDAY_TENANTS["target"]
    const jobs = await fetchWorkdayJobs(config, "Android", 1)
    const testJob = jobs[0]
    const detail = await fetchWorkdayJobDetail(config, testJob.externalPath)
    expect(detail).not.toBeNull()
    expect(detail.jobPostingInfo).toBeDefined()
    expect(detail.jobPostingInfo.title).toBe(testJob.title)
  })
})
