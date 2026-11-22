package dev.fnc314.aijobtracker.frontend.core.network

import dev.fnc314.aijobtracker.frontend.core.auth.AuthTokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

/**
 * Factory creating platform-specific Ktor [HttpClient] engine.
 */
public expect fun createPlatformHttpClient(): HttpClient

/**
 * Custom Ktor client plugin attaching bearer authentication if an [AuthTokenProvider] is present.
 */
public val BearerAuthPlugin: io.ktor.client.plugins.api.ClientPlugin<BearerAuthConfig> =
  createClientPlugin("BearerAuthPlugin", ::BearerAuthConfig) {
    val tokenProvider = pluginConfig.tokenProvider
    onRequest { request, _ ->
      val token = tokenProvider?.getIdToken()
      if (!token.isNullOrBlank()) {
        request.header("Authorization", "Bearer $token")
      } else {
        RemoteNetworkConfig.apiKey?.let { key ->
          if (key.isNotBlank()) {
            request.header(RemoteNetworkConfig.API_KEY_HEADER, key)
          }
        }
      }
    }
  }

public class BearerAuthConfig {
  public var tokenProvider: AuthTokenProvider? = null
}

/**
 * Creates and configures a multiplatform JSON-enabled [HttpClient] with content negotiation
 * and automatic Firebase ID token / API key authentication.
 */
public fun createJsonHttpClient(tokenProvider: AuthTokenProvider? = null): HttpClient {
  return createPlatformHttpClient().config {
    install(ContentNegotiation) {
      json(
        Json {
          ignoreUnknownKeys = true
          prettyPrint = true
          isLenient = true
        },
      )
    }
    install(DefaultRequest)
    if (tokenProvider != null) {
      install(BearerAuthPlugin) {
        this.tokenProvider = tokenProvider
      }
    } else {
      install(DefaultRequest) {
        RemoteNetworkConfig.apiKey?.let { key ->
          if (key.isNotBlank()) {
            header(RemoteNetworkConfig.API_KEY_HEADER, key)
          }
        }
      }
    }
    installInspector()
  }
}

