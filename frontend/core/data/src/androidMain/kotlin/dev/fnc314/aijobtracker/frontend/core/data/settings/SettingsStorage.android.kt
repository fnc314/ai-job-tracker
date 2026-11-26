package dev.fnc314.aijobtracker.frontend.core.data.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dev.fnc314.aijobtracker.frontend.core.data.ApplicationContextHolder
import dev.fnc314.aijobtracker.frontend.core.logging.AppLogger
import io.github.osipxd.security.crypto.encryptedPreferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Creates the platform-native [SettingsStorage] instance.
 *
 * On Android, this resolves to an Encrypted DataStore instance backed by Android Keystore AEAD.
 */
public actual fun createSettingsStorage(): SettingsStorage {
    val context = ApplicationContextHolder.application
    return if (context != null) {
        AndroidEncryptedDataStoreSettingsStorage(context)
    } else {
        InMemorySettingsStorage()
    }
}

private val Context.encryptedDataStore: DataStore<Preferences> by encryptedPreferencesDataStore(
    name = "job_tracker_encrypted_prefs"
)

/**
 * Android implementation of [SettingsStorage] utilizing Jetpack DataStore backed by
 * hardware-backed Keystore encryption via [encryptedPreferencesDataStore].
 *
 * Includes automatic one-time migration from legacy plaintext [android.content.SharedPreferences].
 */
private class AndroidEncryptedDataStoreSettingsStorage(private val context: Context) : SettingsStorage {

    init {
        migrateLegacySharedPreferencesIfPresent()
    }

    override fun getString(key: String): String? {
        val prefKey = stringPreferencesKey(key)
        return try {
            runBlocking {
                context.encryptedDataStore.data.map { preferences -> preferences[prefKey] }.firstOrNull()
            }
        } catch (t: Throwable) {
            AppLogger.e("EncryptedDataStore") { "Error reading key $key from EncryptedDataStore: ${t.message}" }
            null
        }
    }

    override fun putString(key: String, value: String) {
        val prefKey = stringPreferencesKey(key)
        try {
            runBlocking {
                context.encryptedDataStore.edit { preferences ->
                    preferences[prefKey] = value
                }
            }
        } catch (t: Throwable) {
            AppLogger.e("EncryptedDataStore") { "Error writing key $key to EncryptedDataStore: ${t.message}" }
        }
    }

    private fun migrateLegacySharedPreferencesIfPresent() {
        try {
            val legacyPrefs = context.getSharedPreferences("job_tracker_prefs", Context.MODE_PRIVATE)
            val allEntries = legacyPrefs.all
            if (allEntries.isNotEmpty()) {
                AppLogger.i("EncryptedDataStore") {
                    "Migrating ${allEntries.size} legacy preferences to EncryptedDataStore..."
                }
                runBlocking {
                    context.encryptedDataStore.edit { preferences ->
                        allEntries.forEach { (k, v) ->
                            if (v is String) {
                                preferences[stringPreferencesKey(k)] = v
                            }
                        }
                    }
                }
                legacyPrefs.edit().clear().apply()
                AppLogger.i("EncryptedDataStore") { "Migration completed and legacy preferences cleared." }
            }
        } catch (t: Throwable) {
            AppLogger.w("EncryptedDataStore") { "Legacy preference migration skipped/failed: ${t.message}" }
        }
    }
}

private class InMemorySettingsStorage : SettingsStorage {
    private val map = mutableMapOf<String, String>()
    override fun getString(key: String): String? = map[key]
    override fun putString(key: String, value: String) { map[key] = value }
}
