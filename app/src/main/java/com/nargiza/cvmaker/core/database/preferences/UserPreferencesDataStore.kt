package com.nargiza.cvmaker.core.database.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nargiza.cvmaker.core.model.TemplateType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class UserPreferences(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isOnboardingCompleted: Boolean = false,
    val defaultTemplateId: String = TemplateType.MODERN.id,
    val aiConsentGiven: Boolean = false,
    val backendUrl: String = "http://10.0.2.2:8080" // Default for Android emulator to host
)

class UserPreferencesDataStore(private val context: Context) {

    private val themeKey = stringPreferencesKey("theme_mode")
    private val onboardingKey = booleanPreferencesKey("onboarding_completed")
    private val templateKey = stringPreferencesKey("default_template")
    private val aiConsentKey = booleanPreferencesKey("ai_consent_given")
    private val backendUrlKey = stringPreferencesKey("backend_url")

    val preferencesFlow: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val themeStr = prefs[themeKey] ?: ThemeMode.SYSTEM.name
        val themeMode = runCatching { ThemeMode.valueOf(themeStr) }.getOrDefault(ThemeMode.SYSTEM)
        val isOnboarding = prefs[onboardingKey] ?: false
        val defaultTemplate = prefs[templateKey] ?: TemplateType.MODERN.id
        val aiConsent = prefs[aiConsentKey] ?: false
        val backendUrl = prefs[backendUrlKey] ?: "http://10.0.2.2:8080"

        UserPreferences(
            themeMode = themeMode,
            isOnboardingCompleted = isOnboarding,
            defaultTemplateId = defaultTemplate,
            aiConsentGiven = aiConsent,
            backendUrl = backendUrl
        )
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.dataStore.edit { prefs ->
            prefs[themeKey] = mode.name
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[onboardingKey] = completed
        }
    }

    suspend fun setDefaultTemplate(templateId: String) {
        context.dataStore.edit { prefs ->
            prefs[templateKey] = templateId
        }
    }

    suspend fun setAiConsent(consented: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[aiConsentKey] = consented
        }
    }

    suspend fun setBackendUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[backendUrlKey] = url
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
