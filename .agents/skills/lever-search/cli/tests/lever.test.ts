import { describe, expect, test } from "bun:test"
import { fetchLeverPostings, fetchLeverPostingDetail } from "../src/helpers.js"

describe("lever-search", () => {
  test("fetchLeverPostings returns valid postings for public board", async () => {
    const postings = await fetchLeverPostings("spotify")
    expect(Array.isArray(postings)).toBe(true)
    expect(postings.length).toBeGreaterThan(0)
    const first = postings[0]
    expect(first.id).toBeDefined()
    expect(first.text).toBeDefined()
    expect(first.hostedUrl).toContain("lever.co/spotify")
  })

  test("fetchLeverPostingDetail returns valid posting description", async () => {
    const postings = await fetchLeverPostings("spotify")
    const testPost = postings[0]
    const detail = await fetchLeverPostingDetail("spotify", testPost.id)
    expect(detail).not.toBeNull()
    expect(detail?.text).toBe(testPost.text)
    expect(detail?.hostedUrl).toBe(testPost.hostedUrl)
  })
})
