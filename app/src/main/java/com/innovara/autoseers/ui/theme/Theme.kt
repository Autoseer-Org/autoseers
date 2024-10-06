package com.example.compose

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.ui.theme.AppTypography
import com.innovara.autoseers.ui.theme.backgroundDark
import com.innovara.autoseers.ui.theme.backgroundLight
import com.innovara.autoseers.ui.theme.errorContainerDark
import com.innovara.autoseers.ui.theme.errorContainerLight
import com.innovara.autoseers.ui.theme.errorDark
import com.innovara.autoseers.ui.theme.errorLight
import com.innovara.autoseers.ui.theme.inverseOnSurfaceDark
import com.innovara.autoseers.ui.theme.inverseOnSurfaceLight
import com.innovara.autoseers.ui.theme.inversePrimaryDark
import com.innovara.autoseers.ui.theme.inversePrimaryLight
import com.innovara.autoseers.ui.theme.inverseSurfaceDark
import com.innovara.autoseers.ui.theme.inverseSurfaceLight
import com.innovara.autoseers.ui.theme.onBackgroundDark
import com.innovara.autoseers.ui.theme.onBackgroundLight
import com.innovara.autoseers.ui.theme.onErrorContainerDark
import com.innovara.autoseers.ui.theme.onErrorContainerLight
import com.innovara.autoseers.ui.theme.onErrorDark
import com.innovara.autoseers.ui.theme.onErrorLight
import com.innovara.autoseers.ui.theme.onPrimaryContainerDark
import com.innovara.autoseers.ui.theme.onPrimaryContainerLight
import com.innovara.autoseers.ui.theme.onPrimaryDark
import com.innovara.autoseers.ui.theme.onPrimaryLight
import com.innovara.autoseers.ui.theme.onSecondaryContainerDark
import com.innovara.autoseers.ui.theme.onSecondaryContainerLight
import com.innovara.autoseers.ui.theme.onSecondaryDark
import com.innovara.autoseers.ui.theme.onSecondaryLight
import com.innovara.autoseers.ui.theme.onSurfaceDark
import com.innovara.autoseers.ui.theme.onSurfaceLight
import com.innovara.autoseers.ui.theme.onSurfaceVariantDark
import com.innovara.autoseers.ui.theme.onSurfaceVariantLight
import com.innovara.autoseers.ui.theme.onTertiaryContainerDark
import com.innovara.autoseers.ui.theme.onTertiaryContainerLight
import com.innovara.autoseers.ui.theme.onTertiaryDark
import com.innovara.autoseers.ui.theme.onTertiaryLight
import com.innovara.autoseers.ui.theme.outlineDark
import com.innovara.autoseers.ui.theme.outlineLight
import com.innovara.autoseers.ui.theme.outlineVariantDark
import com.innovara.autoseers.ui.theme.outlineVariantLight
import com.innovara.autoseers.ui.theme.primaryContainerDark
import com.innovara.autoseers.ui.theme.primaryContainerLight
import com.innovara.autoseers.ui.theme.primaryDark
import com.innovara.autoseers.ui.theme.primaryLight
import com.innovara.autoseers.ui.theme.scrimDark
import com.innovara.autoseers.ui.theme.scrimLight
import com.innovara.autoseers.ui.theme.secondaryContainerDark
import com.innovara.autoseers.ui.theme.secondaryContainerLight
import com.innovara.autoseers.ui.theme.secondaryDark
import com.innovara.autoseers.ui.theme.secondaryLight
import com.innovara.autoseers.ui.theme.surfaceDark
import com.innovara.autoseers.ui.theme.surfaceLight
import com.innovara.autoseers.ui.theme.surfaceVariantDark
import com.innovara.autoseers.ui.theme.surfaceVariantLight
import com.innovara.autoseers.ui.theme.tertiaryContainerDark
import com.innovara.autoseers.ui.theme.tertiaryContainerLight
import com.innovara.autoseers.ui.theme.tertiaryDark
import com.innovara.autoseers.ui.theme.tertiaryLight

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
)

@Composable
fun AutoSeersTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable() () -> Unit
) {
    val colorScheme = remember(darkTheme) {
        when {
            darkTheme -> darkScheme
            else -> lightScheme
        }
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

