package com.nargiza.cvmaker.domain.repository

import com.nargiza.cvmaker.core.database.preferences.ThemeMode
import com.nargiza.cvmaker.core.database.preferences.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observePreferences(): Flow<UserPreferences>
    suspend fun setThemeMode(mode: ThemeMode)
    suspend fun setOnboardingCompleted(completed: Boolean)
    suspend fun setDefaultTemplate(templateId: String)
    suspend fun setAiConsent(consented: Boolean)
    suspend fun setBackendUrl(url: String)
    suspend fun deleteAllData()
}
