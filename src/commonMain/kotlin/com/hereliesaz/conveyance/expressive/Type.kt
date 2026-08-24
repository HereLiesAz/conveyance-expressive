package com.hereliesaz.conveyance.expressive

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * M3's real fifteen-step type scale -- five roles (Display/Headline/Title/Body/Label) times three
 * sizes (Large/Medium/Small), values as specified by Material Design 3, not invented here. [family]
 * defaults to [FontFamily.Default]; M3's own default is Roboto specifically, but bundling that
 * typeface's font files is a per-host asset decision this library doesn't make for you, the same
 * choice `conveyance-h2g2` makes for Jost.
 */
data class ExpressiveType(
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle,
)

fun expressiveType(family: FontFamily = FontFamily.Default): ExpressiveType = ExpressiveType(
    displayLarge = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Normal,
        fontSize = 57.sp, lineHeight = 64.sp, letterSpacing = (-0.25).sp,
    ),
    displayMedium = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Normal,
        fontSize = 45.sp, lineHeight = 52.sp, letterSpacing = 0.sp,
    ),
    displaySmall = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Normal,
        fontSize = 36.sp, lineHeight = 44.sp, letterSpacing = 0.sp,
    ),
    headlineLarge = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Normal,
        fontSize = 32.sp, lineHeight = 40.sp, letterSpacing = 0.sp,
    ),
    headlineMedium = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Normal,
        fontSize = 28.sp, lineHeight = 36.sp, letterSpacing = 0.sp,
    ),
    headlineSmall = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Normal,
        fontSize = 24.sp, lineHeight = 32.sp, letterSpacing = 0.sp,
    ),
    titleLarge = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Normal,
        fontSize = 22.sp, lineHeight = 28.sp, letterSpacing = 0.sp,
    ),
    titleMedium = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Medium,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.15.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Normal,
        fontSize = 16.sp, lineHeight = 24.sp, letterSpacing = 0.5.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Normal,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.25.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Normal,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.4.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Medium,
        fontSize = 14.sp, lineHeight = 20.sp, letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Medium,
        fontSize = 12.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = family, fontWeight = FontWeight.Medium,
        fontSize = 11.sp, lineHeight = 16.sp, letterSpacing = 0.5.sp,
    ),
)

/**
 * Looks up a step by the composable manifest's `scale` string (azphalt `spec/composable.md`).
 * Accepts M3's own role names lowerCamelCased (`"titleMedium"`), and h2g2-style aliases
 * (`hero`/`section`/`lead`/`body`/`eyebrow`/`micro`) so a manifest authored against either
 * composable-set's `scale` vocabulary resolves sensibly here too.
 */
fun ExpressiveType.step(name: String): TextStyle = when (name) {
    "displayLarge" -> displayLarge
    "displayMedium", "hero" -> displayMedium
    "displaySmall" -> displaySmall
    "headlineLarge", "section" -> headlineLarge
    "headlineMedium" -> headlineMedium
    "headlineSmall" -> headlineSmall
    "titleLarge", "lead" -> titleLarge
    "titleMedium" -> titleMedium
    "titleSmall", "capsule" -> titleSmall
    "bodyLarge" -> bodyLarge
    "bodyMedium", "body" -> bodyMedium
    "bodySmall" -> bodySmall
    "labelLarge", "eyebrow" -> labelLarge
    "labelMedium", "endCap" -> labelMedium
    "labelSmall", "micro" -> labelSmall
    else -> bodyMedium
}
