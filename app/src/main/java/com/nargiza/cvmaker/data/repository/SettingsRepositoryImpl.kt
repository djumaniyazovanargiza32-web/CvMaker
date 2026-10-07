package com.nargiza.cvmaker.data.repository

import com.nargiza.cvmaker.core.common.DispatchersProvider
import com.nargiza.cvmaker.core.database.dao.CvDao
import com.nargiza.cvmaker.core.database.preferences.ThemeMode
import com.nargiza.cvmaker.core.database.preferences.UserPreferences
import com.nargiza.cvmaker.core.database.preferences.UserPreferencesDataStore
import com.nargiza.cvmaker.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class SettingsRepositoryImpl(
    private val dataStore: UserPreferencesDataStore,
    private val cvDao: CvDao,
    private val dispatchers: DispatchersProvider
) : SettingsRepository {

    override fun observePreferences(): Flow<UserPreferences> {
        return dataStore.preferencesFlow.flowOn(dispatchers.io)
    }

    override suspend fun setThemeMode(mode: ThemeMode) = withContext(dispatchers.io) {
        dataStore.setThemeMode(mode)
    }

    override suspend fun setOnboardingCompleted(completed: Boolean) = withContext(dispatchers.io) {
        dataStore.setOnboardingCompleted(completed)
    }

    override suspend fun setDefaultTemplate(templateId: String) = withContext(dispatchers.io) {
        dataStore.setDefaultTemplate(templateId)
    }

    override suspend fun setAiConsent(consented: Boolean) = withContext(dispatchers.io) {
        dataStore.setAiConsent(consented)
    }

    override suspend fun setBackendUrl(url: String) = withContext(dispatchers.io) {
        dataStore.setBackendUrl(url)
    }

    override suspend fun deleteAllData(): Unit = withContext(dispatchers.io) {
        cvDao.deleteAll()
        dataStore.clearAll()
    }
}
