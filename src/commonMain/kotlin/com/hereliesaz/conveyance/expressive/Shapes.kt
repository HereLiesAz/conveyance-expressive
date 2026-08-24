package com.hereliesaz.conveyance.expressive

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.graphics.shapes.RoundedPolygon

/**
 * A curated subset of M3 Expressive's 35 [MaterialShapes] polygons, named for the composable
 * manifest's `surface` string (azphalt `spec/composable.md`) rather than M3's own constant names
 * -- a host reaches for what an element IS ("a badge", "a spark"), not which of the 35 polygons
 * renders it.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
object ExpressiveSurface {
    val badge: RoundedPolygon = MaterialShapes.Circle
    val pill: RoundedPolygon = MaterialShapes.Pill
    val bloom: RoundedPolygon = MaterialShapes.Clover4Leaf
    val spark: RoundedPolygon = MaterialShapes.Sunny
    val burst: RoundedPolygon = MaterialShapes.Boom
    val gem: RoundedPolygon = MaterialShapes.Gem
    val arch: RoundedPolygon = MaterialShapes.Arch

    /** The "busy" shape [MorphControl][com.hereliesaz.conveyance.expressive.MorphControl] morphs toward while yielding. */
    val cookie: RoundedPolygon = MaterialShapes.Cookie9Sided

    /** Looks up a polygon by the composable manifest's `surface` string. */
    fun byName(name: String): RoundedPolygon = when (name) {
        "badge" -> badge
        "pill" -> pill
        "bloom" -> bloom
        "spark" -> spark
        "burst" -> burst
        "gem" -> gem
        "arch" -> arch
        "cookie" -> cookie
        else -> badge
    }

    /** [byName] converted to a static Compose [Shape], for a non-morphing element. */
    @Composable
    fun shapeOf(name: String): Shape = byName(name).toShape()
}
