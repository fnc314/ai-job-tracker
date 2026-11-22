package dev.fnc314.aijobtracker.frontend.core.data.source.impl

import dev.fnc314.aijobtracker.frontend.core.auth.AuthTokenProvider
import dev.fnc314.aijobtracker.frontend.core.data.model.DevicePlatform
import dev.fnc314.aijobtracker.frontend.core.data.model.PlatformDeviceInfo
import dev.fnc314.aijobtracker.frontend.core.data.source.NetworkDataSource
import dev.fnc314.aijobtracker.frontend.core.data.source.NotificationRemoteDataSource
import dev.fnc314.aijobtracker.frontend.core.logging.AppLogger
import dev.fnc314.aijobtracker.frontend.core.network.RemoteNetworkConfig
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal class NotificationRemoteDataSourceImpl(
    private val networkDataSource: NetworkDataSource,
    private val authTokenProvider: AuthTokenProvider? = null,
) : NotificationRemoteDataSource {

    private suspend fun headers(): Map<String, String> {
        val result = mutableMapOf<String, String>()
        val token = authTokenProvider?.getIdToken()
        if (!token.isNullOrBlank()) {
            result["Authorization"] = "Bearer $token"
        } else {
            val apiKey = RemoteNetworkConfig.apiKey
            if (!apiKey.isNullOrBlank()) {
                result[RemoteNetworkConfig.API_KEY_HEADER] = apiKey
            }
        }
        return result
    }

    override suspend fun registerDevice(token: String, platform: DevicePlatform, installationId: String): Boolean {
        return try {
            val payload = buildJsonObject {
                put("token", token)
                put("platform", platform.name)
                put("installationId", installationId)
            }.toString()

            val url = "${RemoteNetworkConfig.baseUrl}/notifications/device/register"
            AppLogger.d("NotificationRemoteDataSource") { "POST $url payload: $payload" }
            val response = networkDataSource.postJson(url = url, jsonBody = payload, headers = headers())
            AppLogger.i("NotificationRemoteDataSource") { "POST $url response: $response" }
            true
        } catch (t: Throwable) {
            AppLogger.e("NotificationRemoteDataSource") { "Failed to register device: ${t.message}" }
            false
        }
    }

    override suspend fun deregisterDevice(installationId: String): Boolean {
        return try {
            val payload = buildJsonObject {
                put("installationId", installationId)
            }.toString()

            val url = "${RemoteNetworkConfig.baseUrl}/notifications/device/deregister"
            networkDataSource.postJson(url = url, jsonBody = payload, headers = headers())
            true
        } catch (_: Throwable) {
            false
        }
    }

    override suspend fun sendHeartbeat(
        deviceInfo: PlatformDeviceInfo,
        targetProposalId: String?,
    ): Boolean {
        return try {
            val payload = buildJsonObject {
                put("installationId", deviceInfo.installationId)
                put("platform", deviceInfo.platform.name)
                put("make", deviceInfo.make)
                put("model", deviceInfo.model)
                put("osVersion", deviceInfo.osVersion)
                put("appVersion", deviceInfo.appVersion)
                if (!targetProposalId.isNullOrBlank()) {
                    put("targetProposalId", targetProposalId)
                }
            }.toString()

            val url = "${RemoteNetworkConfig.baseUrl}/notifications/device/heartbeat"
            networkDataSource.postJson(url = url, jsonBody = payload, headers = headers())
            true
        } catch (_: Throwable) {
            false
        }
    }
}
