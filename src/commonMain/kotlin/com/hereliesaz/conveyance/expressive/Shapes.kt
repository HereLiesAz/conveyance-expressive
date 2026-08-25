package com.hereliesaz.conveyance.expressive

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.Cubic
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
    fun shapeOf(name: String): Shape = RoundedPolygonShape(byName(name))
}

/**
 * Builds [path] from a Bezier-cubic outline (as returned by [RoundedPolygon.cubics] or
 * [androidx.graphics.shapes.Morph.asCubics]), rewound and closed. Written locally rather than
 * reused from material3's own (near-identical) internal helper of the same name: material3's
 * public `toPath()`/`toShape()` default their own `Path()`, one that resolves, off Android, to a
 * stub implementation that throws `NotImplementedError` -- that default is baked into material3's
 * own compiled bytecode and unavoidable through those entry points. Mutating a caller-supplied
 * real [Path] through nothing but its own public interface methods sidesteps that entirely.
 */
internal fun pathFromCubics(path: Path, cubics: List<Cubic>): Path {
    path.rewind()
    cubics.forEachIndexed { index, cubic ->
        if (index == 0) path.moveTo(cubic.anchor0X, cubic.anchor0Y)
        path.cubicTo(
            cubic.control0X, cubic.control0Y,
            cubic.control1X, cubic.control1Y,
            cubic.anchor1X, cubic.anchor1Y,
        )
    }
    path.close()
    return path
}

/** A static (non-morphing) [Shape] for one [RoundedPolygon], scaled to fill its assigned box. */
internal class RoundedPolygonShape(polygon: RoundedPolygon) : Shape {
    private val shapePath: Path = pathFromCubics(Path(), polygon.cubics)

    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = Path().apply { addPath(shapePath) }
        path.transform(Matrix().apply { scale(x = size.width, y = size.height) })
        // Recenter on the box rather than assuming the scaled path already sits at (0,0):
        // MaterialShapes polygons are `.normalized()`-ed into (0,0)-(1,1), but not perfectly
        // centered there for every one of the 35 shapes.
        val bounds = path.getBounds()
        path.translate(Offset(size.width / 2f - bounds.center.x, size.height / 2f - bounds.center.y))
        return Outline.Generic(path)
    }
}
