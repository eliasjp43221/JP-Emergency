package com.example.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.models.Upazila
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "jamalpur_sheba_prefs")

enum class ThemeMode(val id: String, val labelBn: String) {
    SYSTEM("SYSTEM", "সিস্টেম ডিফল্ট"),
    LIGHT("LIGHT", "লাইট থিম (হালকা)"),
    DARK("DARK", "ডার্ক থিম (গাঢ়)");

    companion object {
        fun fromId(id: String): ThemeMode = entries.find { it.id == id } ?: SYSTEM
    }
}

data class UserPreferencesState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val defaultUpazila: Upazila = Upazila.ALL,
    val showSampleDonors: Boolean = true,
    val datasetVersion: String = "1.0.0"
)

class UserPreferencesRepository(private val context: Context) {

    private object Keys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val DEFAULT_UPAZILA = stringPreferencesKey("default_upazila")
        val SHOW_SAMPLE_DONORS = booleanPreferencesKey("show_sample_donors")
        val DATASET_VERSION = stringPreferencesKey("dataset_version")
    }

    val preferencesFlow: Flow<UserPreferencesState> = context.dataStore.data.map { prefs ->
        UserPreferencesState(
            themeMode = ThemeMode.fromId(prefs[Keys.THEME_MODE] ?: ThemeMode.SYSTEM.id),
            defaultUpazila = Upazila.fromId(prefs[Keys.DEFAULT_UPAZILA] ?: Upazila.ALL.id),
            showSampleDonors = prefs[Keys.SHOW_SAMPLE_DONORS] ?: true,
            datasetVersion = prefs[Keys.DATASET_VERSION] ?: "1.0.0"
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[Keys.THEME_MODE] = mode.id
        }
    }

    suspend fun setDefaultUpazila(upazila: Upazila) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DEFAULT_UPAZILA] = upazila.id
        }
    }

    suspend fun setShowSampleDonors(show: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.SHOW_SAMPLE_DONORS] = show
        }
    }

    suspend fun setDatasetVersion(version: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DATASET_VERSION] = version
        }
    }
}
