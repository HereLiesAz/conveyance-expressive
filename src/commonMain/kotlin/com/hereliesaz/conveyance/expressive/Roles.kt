package com.hereliesaz.conveyance.expressive

import androidx.compose.ui.graphics.Color

/**
 * M3 Expressive's color-role thinking -- primary/secondary/tertiary -- lines up with Conveyance's
 * own [com.hereliesaz.conveyance.Rank] more directly than h2g2's per-entity Identify hue does
 * ([com.hereliesaz.conveyance.Channel.Hue] already carries [com.hereliesaz.conveyance.Meaning.SemanticRank]).
 * So this composable-set's `hue` manifest field (azphalt `spec/composable.md`) selects a rank
 * container color rather than a hashed identity color. Values are M3's own well-known baseline
 * seed-color container tones, not invented here.
 */
object ExpressiveRole {
    val primaryContainer: Color = Color(0xFFEADDFF)
    val onPrimaryContainer: Color = Color(0xFF21005D)
    val secondaryContainer: Color = Color(0xFFE8DEF8)
    val onSecondaryContainer: Color = Color(0xFF1D192B)
    val tertiaryContainer: Color = Color(0xFFFFD8E4)
    val onTertiaryContainer: Color = Color(0xFF31111D)

    /** Looks up a container color by the composable manifest's `hue` string ("primary"/"secondary"/"tertiary"). */
    fun containerOf(rank: String): Color = when (rank) {
        "primary" -> primaryContainer
        "tertiary" -> tertiaryContainer
        else -> secondaryContainer
    }

    /** The readable-on-[containerOf] counterpart. */
    fun onContainerOf(rank: String): Color = when (rank) {
        "primary" -> onPrimaryContainer
        "tertiary" -> onTertiaryContainer
        else -> onSecondaryContainer
    }
}
