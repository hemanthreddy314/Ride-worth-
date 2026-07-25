package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.example.data.models.VehicleType

private val AutomotiveColorScheme = darkColorScheme(
    primary = ChampagneGold,
    onPrimary = PrimaryBackground,
    primaryContainer = ChampagneGoldDark,
    onPrimaryContainer = TextPrimary,
    secondary = DeepSapphire,
    onSecondary = TextPrimary,
    tertiary = ChampagneGold,
    background = PrimaryBackground,
    onBackground = TextPrimary,
    surface = SecondaryBackground,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline = SoftBorderColor,
    outlineVariant = DividerColor
)

private val AutomotiveLightColorScheme = lightColorScheme(
    primary = ChampagneGold,
    onPrimary = PrimaryBackground,
    primaryContainer = Color(0xFFF5E8C7),
    onPrimaryContainer = PrimaryBackground,
    secondary = DeepSapphire,
    onSecondary = Color.White,
    tertiary = ChampagneGoldDark,
    background = Color(0xFFF4F4F2),
    onBackground = Color(0xFF111315),
    surface = Color(0xFFEAEAEA),
    onSurface = Color(0xFF111315),
    surfaceVariant = Color(0xFFE0E3E8),
    onSurfaceVariant = Color(0xFF5A6270),
    outline = Color(0xFFC4C8D0),
    outlineVariant = Color(0xFFD6DADF)
)

@Composable
fun RideWorthTheme(
    darkTheme: Boolean = true,
    vehicleType: VehicleType = VehicleType.CAR,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) AutomotiveColorScheme else AutomotiveLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = RideWorthTypography,
        shapes = RideWorthShapes,
        content = content
    )
}
