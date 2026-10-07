package com.nargiza.cvmaker.core.designsystem

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = CvmakerColors.PrimaryDark,
    onPrimary = CvmakerColors.OnPrimaryDark,
    primaryContainer = CvmakerColors.PrimaryContainerDark,
    onPrimaryContainer = CvmakerColors.OnPrimaryContainerDark,
    secondary = CvmakerColors.SecondaryDark,
    onSecondary = CvmakerColors.OnSecondaryDark,
    secondaryContainer = CvmakerColors.SecondaryContainerDark,
    onSecondaryContainer = CvmakerColors.OnSecondaryContainerDark,
    tertiary = CvmakerColors.TertiaryDark,
    onTertiary = CvmakerColors.OnTertiaryDark,
    tertiaryContainer = CvmakerColors.TertiaryContainerDark,
    onTertiaryContainer = CvmakerColors.OnTertiaryContainerDark,
    background = CvmakerColors.BackgroundDark,
    onBackground = CvmakerColors.OnBackgroundDark,
    surface = CvmakerColors.SurfaceDark,
    onSurface = CvmakerColors.OnSurfaceDark,
    surfaceVariant = CvmakerColors.SurfaceVariantDark,
    onSurfaceVariant = CvmakerColors.OnSurfaceVariantDark,
    error = CvmakerColors.ErrorDark,
    onError = CvmakerColors.OnErrorDark
)

private val LightColorScheme = lightColorScheme(
    primary = CvmakerColors.PrimaryLight,
    onPrimary = CvmakerColors.OnPrimaryLight,
    primaryContainer = CvmakerColors.PrimaryContainerLight,
    onPrimaryContainer = CvmakerColors.OnPrimaryContainerLight,
    secondary = CvmakerColors.SecondaryLight,
    onSecondary = CvmakerColors.OnSecondaryLight,
    secondaryContainer = CvmakerColors.SecondaryContainerLight,
    onSecondaryContainer = CvmakerColors.OnSecondaryContainerLight,
    tertiary = CvmakerColors.TertiaryLight,
    onTertiary = CvmakerColors.OnTertiaryLight,
    tertiaryContainer = CvmakerColors.TertiaryContainerLight,
    onTertiaryContainer = CvmakerColors.OnTertiaryContainerLight,
    background = CvmakerColors.BackgroundLight,
    onBackground = CvmakerColors.OnBackgroundLight,
    surface = CvmakerColors.SurfaceLight,
    onSurface = CvmakerColors.OnSurfaceLight,
    surfaceVariant = CvmakerColors.SurfaceVariantLight,
    onSurfaceVariant = CvmakerColors.OnSurfaceVariantLight,
    error = CvmakerColors.ErrorLight,
    onError = CvmakerColors.OnErrorLight
)

@Composable
fun CvmakerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep default consistent brand colors unless requested
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CvmakerTypography,
        shapes = CvmakerShapes,
        content = content
    )
}
