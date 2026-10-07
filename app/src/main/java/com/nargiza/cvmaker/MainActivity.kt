package com.nargiza.cvmaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.nargiza.cvmaker.core.database.preferences.ThemeMode
import com.nargiza.cvmaker.core.database.preferences.UserPreferences
import com.nargiza.cvmaker.core.designsystem.CvmakerTheme
import com.nargiza.cvmaker.domain.repository.SettingsRepository
import com.nargiza.cvmaker.navigation.CvNavHost
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    private val settingsRepository: SettingsRepository by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val prefs by settingsRepository.observePreferences().collectAsStateWithLifecycle(
                initialValue = UserPreferences()
            )

            val darkTheme = when (prefs.themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }

            CvmakerTheme(darkTheme = darkTheme) {
                CvNavHost(
                    isOnboardingCompleted = prefs.isOnboardingCompleted,
                    settingsRepository = settingsRepository
                )
            }
        }
    }
}