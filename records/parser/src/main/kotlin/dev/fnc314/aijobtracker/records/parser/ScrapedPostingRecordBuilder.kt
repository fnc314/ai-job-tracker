package dev.fnc314.aijobtracker.records.parser

import com.google.protobuf.Timestamp
import dev.fnc314.aijobtracker.records.ScrapedPosting
import dev.fnc314.aijobtracker.records.ScrapedPostingStatus
import java.time.Instant

/**
 * Functional builder and mapper for [ScrapedPosting] protobuf messages.
 *
 * Provides factory functions to create, map, and serialize scraped job records
 * into Firestore-ready document maps and Protobuf objects, treating them with
 * identical significance and rigor to [dev.fnc314.aijobtracker.records.JobApplication].
 */
public object ScrapedPostingRecordBuilder {

  /**
   * Constructs a [ScrapedPosting] protobuf message from raw portal scraping fields.
   *
   * @param id Unique identifier or URL hash.
   * @param title Title of the position.
   * @param company Employer name.
   * @param url Canonical job posting URL.
   * @param portal Origin scraper tool / portal identifier.
   * @param location Geographic location or remote status.
   * @param description Full job description text.
   * @param postingDate Optional publication date.
   * @param fitRating Evaluated match score (0..100).
   * @param fitStatus Scraped posting lifecycle state.
   * @return Fully populated [ScrapedPosting] protobuf message.
   */
  public fun create(
    id: String,
    title: String,
    company: String,
    url: String,
    portal: String,
    location: String = "",
    description: String = "",
    postingDate: String = "",
    fitRating: Int = 0,
    fitStatus: ScrapedPostingStatus = ScrapedPostingStatus.SCRAPED_POSTING_STATUS_UNSPECIFIED,
  ): ScrapedPosting {
    val now = Instant.now()
    val protoNow = Timestamp.newBuilder()
      .setSeconds(now.epochSecond)
      .setNanos(now.nano)
      .build()

    val uuidVal = if (id.isNotBlank()) id else UuidGenerator.newUuid()
    val companySlugVal = DocumentIdGenerator.normalizeDirName(company)
    val titleSlugVal = DocumentIdGenerator.normalizeDirName(title)
    val dedupKeyVal = "$companySlugVal--$titleSlugVal"
    val normalizedUrlVal = UrlSanitizer.cleanUrl(url)

    return ScrapedPosting.newBuilder()
      .setUuid(uuidVal)
      .setDedupKey(dedupKeyVal)
      .setNormalizedUrl(normalizedUrlVal)
      .setCompanySlug(companySlugVal)
      .setTitleSlug(titleSlugVal)
      .setTitle(title)
      .setCompany(company)
      .setUrl(url)
      .setLocation(location)
      .setPortal(portal)
      .setDescription(description)
      .setPostingDate(postingDate)
      .setFitRating(fitRating)
      .setFitStatus(fitStatus)
      .setFirstScrapedAt(protoNow)
      .setLastScrapedAt(protoNow)
      .setScrapeCount(1)
      .setCreatedAt(protoNow)
      .setUpdatedAt(protoNow)
      .build()
  }

  /**
   * Converts a [ScrapedPosting] protobuf message to a Firestore document representation.
   */
  public fun toFirestoreMap(posting: ScrapedPosting): Map<String, Any?> = mapOf(
    "uuid" to posting.uuid,
    "dedupKey" to posting.dedupKey,
    "normalizedUrl" to posting.normalizedUrl,
    "companySlug" to posting.companySlug,
    "titleSlug" to posting.titleSlug,
    "title" to posting.title,
    "company" to posting.company,
    "url" to posting.url,
    "location" to posting.location,
    "portal" to posting.portal,
    "postingDate" to posting.postingDate,
    "description" to posting.description,
    "fitRating" to posting.fitRating,
    "fitStatus" to posting.fitStatus.name,
    "scrapeCount" to posting.scrapeCount,
    "createdAt" to posting.createdAt.seconds,
    "updatedAt" to posting.updatedAt.seconds,
  )
}
