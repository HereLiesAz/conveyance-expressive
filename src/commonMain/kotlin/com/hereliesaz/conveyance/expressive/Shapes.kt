package com.hereliesaz.conveyance.expressive

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Shape
import androidx.graphics.shapes.RoundedPolygon

/**
 * M3 Expressive's full 35-polygon [MaterialShapes] vocabulary, named for the composable
 * manifest's `surface` string (azphalt `spec/composable.md`) with M3's own constant names
 * lowercased-first-letter (`Cookie9Sided` -> `"cookie9Sided"`) so a host can reach for any of
 * them directly, plus a handful of friendlier aliases (`badge`, `bloom`, `spark`) kept for
 * continuity with this library's first release.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
object ExpressiveSurface {
    val circle: RoundedPolygon = MaterialShapes.Circle
    val square: RoundedPolygon = MaterialShapes.Square
    val slanted: RoundedPolygon = MaterialShapes.Slanted
    val arch: RoundedPolygon = MaterialShapes.Arch
    val fan: RoundedPolygon = MaterialShapes.Fan
    val arrow: RoundedPolygon = MaterialShapes.Arrow
    val semiCircle: RoundedPolygon = MaterialShapes.SemiCircle
    val oval: RoundedPolygon = MaterialShapes.Oval
    val pill: RoundedPolygon = MaterialShapes.Pill
    val triangle: RoundedPolygon = MaterialShapes.Triangle
    val diamond: RoundedPolygon = MaterialShapes.Diamond
    val clamShell: RoundedPolygon = MaterialShapes.ClamShell
    val pentagon: RoundedPolygon = MaterialShapes.Pentagon
    val gem: RoundedPolygon = MaterialShapes.Gem
    val sunny: RoundedPolygon = MaterialShapes.Sunny
    val verySunny: RoundedPolygon = MaterialShapes.VerySunny
    val cookie4Sided: RoundedPolygon = MaterialShapes.Cookie4Sided
    val cookie6Sided: RoundedPolygon = MaterialShapes.Cookie6Sided
    val cookie7Sided: RoundedPolygon = MaterialShapes.Cookie7Sided
    val cookie9Sided: RoundedPolygon = MaterialShapes.Cookie9Sided
    val cookie12Sided: RoundedPolygon = MaterialShapes.Cookie12Sided
    val ghostish: RoundedPolygon = MaterialShapes.Ghostish
    val clover4Leaf: RoundedPolygon = MaterialShapes.Clover4Leaf
    val clover8Leaf: RoundedPolygon = MaterialShapes.Clover8Leaf
    val burst: RoundedPolygon = MaterialShapes.Burst
    val softBurst: RoundedPolygon = MaterialShapes.SoftBurst
    val boom: RoundedPolygon = MaterialShapes.Boom
    val softBoom: RoundedPolygon = MaterialShapes.SoftBoom
    val flower: RoundedPolygon = MaterialShapes.Flower
    val puffy: RoundedPolygon = MaterialShapes.Puffy
    val puffyDiamond: RoundedPolygon = MaterialShapes.PuffyDiamond
    val pixelCircle: RoundedPolygon = MaterialShapes.PixelCircle
    val pixelTriangle: RoundedPolygon = MaterialShapes.PixelTriangle
    val bun: RoundedPolygon = MaterialShapes.Bun
    val heart: RoundedPolygon = MaterialShapes.Heart

    /** Kept from this library's first release, before the full 35-shape vocabulary was named directly. */
    val badge: RoundedPolygon get() = circle
    val bloom: RoundedPolygon get() = clover4Leaf
    val spark: RoundedPolygon get() = sunny

    /** The "busy" shape [MorphControl][com.hereliesaz.conveyance.expressive.MorphControl] morphs toward while yielding. */
    val cookie: RoundedPolygon get() = cookie9Sided

    private val byNameMap: Map<String, RoundedPolygon> = mapOf(
        "circle" to circle, "square" to square, "slanted" to slanted, "arch" to arch,
        "fan" to fan, "arrow" to arrow, "semiCircle" to semiCircle, "oval" to oval,
        "pill" to pill, "triangle" to triangle, "diamond" to diamond, "clamShell" to clamShell,
        "pentagon" to pentagon, "gem" to gem, "sunny" to sunny, "verySunny" to verySunny,
        "cookie4Sided" to cookie4Sided, "cookie6Sided" to cookie6Sided,
        "cookie7Sided" to cookie7Sided, "cookie9Sided" to cookie9Sided,
        "cookie12Sided" to cookie12Sided, "ghostish" to ghostish,
        "clover4Leaf" to clover4Leaf, "clover8Leaf" to clover8Leaf, "burst" to burst,
        "softBurst" to softBurst, "boom" to boom, "softBoom" to softBoom, "flower" to flower,
        "puffy" to puffy, "puffyDiamond" to puffyDiamond, "pixelCircle" to pixelCircle,
        "pixelTriangle" to pixelTriangle, "bun" to bun, "heart" to heart,
        "badge" to badge, "bloom" to bloom, "spark" to spark, "cookie" to cookie,
    )

    /** Looks up a polygon by the composable manifest's `surface` string. */
    fun byName(name: String): RoundedPolygon = byNameMap[name] ?: circle

    /** [byName] converted to a static Compose [Shape], for a non-morphing element. */
    @Composable
    fun shapeOf(name: String): Shape = byName(name).toShape()
}
