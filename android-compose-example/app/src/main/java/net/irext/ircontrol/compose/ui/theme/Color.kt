package net.irext.ircontrol.compose.ui.theme

import androidx.compose.ui.graphics.Color


/**
 * Filename:       Color.kt
 * Created:        Date: 2026-07-04
 *
 * Description:    Defines light and dark color values for the app theme.
 *
 * Revision log:
 * 2026-07-04: created by shdmfire
 */

val PrimaryGray = Color(0xFF3F3F3F)      // Original primary color, deep charcoal gray
val AccentGreen = Color(0xFF4A6F45)      // Slightly desaturated matcha green (original accent was 0xFF3FAF2F)
val PrimaryDark = Color(0xFF1E1E1E)      // Soft black with a hint of gray; easier on the eyes than pure black

val LightPrimary = PrimaryGray
val LightOnPrimary = Color(0xFFFFFFFF)
val LightPrimaryContainer = Color(0xFFF1F1F1) // Light gray button background
val LightOnPrimaryContainer = Color(0xFF1C1C1C)

val LightSecondary = Color(0xFF5A5F65)       // Desaturated blue gray
val LightOnSecondary = Color(0xFFFFFFFF)
val LightSecondaryContainer = Color(0xFFE2E6EC)
val LightOnSecondaryContainer = Color(0xFF171D22)

val LightTertiary = AccentGreen
val LightOnTertiary = Color(0xFFFFFFFF)
val LightTertiaryContainer = Color(0xFFD6E4D5) // Very light green for subtle hints
val LightOnTertiaryContainer = Color(0xFF102511)

val LightBackground = Color(0xFFF9F9FA)       // Near-white light gray that avoids glare
val LightOnBackground = Color(0xFF1A1C1E)
val LightSurface = Color(0xFFF9F9FA)
val LightOnSurface = Color(0xFF1A1C1E)
val LightSurfaceVariant = Color(0xFFE1E2E5)   // Card and group background
val LightOnSurfaceVariant = Color(0xFF44474B) // Secondary text and inactive icons
val LightOutline = Color(0xFF74777F)          // Border stroke

val DarkPrimary = Color(0xFFC6C6C6)
val DarkOnPrimary = Color(0xFF2E2E2E)
val DarkPrimaryContainer = Color(0xFF474747)
val DarkOnPrimaryContainer = Color(0xFFE2E2E2)

val DarkSecondary = Color(0xFFC2C7CE)
val DarkOnSecondary = Color(0xFF2C3137)
val DarkSecondaryContainer = Color(0xFF43474E)
val DarkOnSecondaryContainer = Color(0xFFDFE2E9)

val DarkTertiary = Color(0xFFA5CFA4)
val DarkOnTertiary = Color(0xFF1D3B1C)
val DarkTertiaryContainer = Color(0xFF335232)
val DarkOnTertiaryContainer = Color(0xFFC0ECC0)

val DarkBackground = PrimaryDark
val DarkOnBackground = Color(0xFFE2E2E5)
val DarkSurface = PrimaryDark
val DarkOnSurface = Color(0xFFE2E2E5)
val DarkSurfaceVariant = Color(0xFF44474B)
val DarkOnSurfaceVariant = Color(0xFFC4C7CC)
val DarkOutline = Color(0xFF8E9196)

val LightError = Color(0xFFBA1A1A)
val LightOnError = Color(0xFFFFFFFF)
val DarkError = Color(0xFFFFB4AB)
val DarkOnError = Color(0xFF690005)
