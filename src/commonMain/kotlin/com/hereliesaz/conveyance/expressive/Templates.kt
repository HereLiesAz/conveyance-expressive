package com.hereliesaz.conveyance.expressive

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import com.hereliesaz.conveyance.Act
import com.hereliesaz.conveyance.ActState
import com.hereliesaz.conveyance.compose.Offer
import com.hereliesaz.conveyance.compose.tell

// Every template attaches Modifier.tell(owesTell, weight).clickable { engage() } to its outermost
// shape -- the wiring Conveyance's own demo (conveyance-demo/.../Gallery.kt) uses at every real
// Offer call site. Without it a template still renders correctly but is inert: nothing engages
// the act on tap, so ActState can never leave Ready through this template alone.

/**
 * What a `kind: "composable"` `.azp` package's `elements[]` entry (azphalt `spec/composable.md`)
 * supplies once a host has resolved it against this library's [Templates.registry] and built the
 * live [Act] the element performs. The manifest's `hue` string is a rank ("primary"/"secondary"/
 * "tertiary" -- see [ExpressiveRole]); `surface` names a shape (see [ExpressiveSurface]);
 * `scale` names a type step (see [ExpressiveType.step]); `templateId` is the registry lookup key
 * and isn't repeated here. [subtitle] is optional -- only `expressive.tile.title` uses it.
 */
data class ComposableRequest(
    val act: Act,
    val rank: String,
    val surface: String,
    val scale: String,
    val label: String,
    val subtitle: String? = null,
)

/**
 * The expressive composable-set's template registry -- what a `templateId` resolves against once
 * this artifact is linked at build time. A host looks a `templateId` up here and calls the
 * matching function with the manifest's declared token values.
 */
object Templates {
    val registry: Map<String, @Composable (ComposableRequest) -> Unit> = mapOf(
        "expressive.badge.shape" to { request -> ShapeBadge(request) },
        "expressive.badge.compound" to { request -> CompoundBadge(request) },
        "expressive.control.morph" to { request -> MorphControl(request) },
        "expressive.tile.title" to { request -> TitleTile(request) },
    )
}

/** A static M3-Expressive-polygon-shaped badge, colored by [ComposableRequest.rank], labeled at [ComposableRequest.scale]. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ShapeBadge(request: ComposableRequest) {
    val shape = ExpressiveSurface.shapeOf(request.surface)
    Offer(act = request.act) {
        Box(
            modifier = Modifier
                .tell(owesTell, weight)
                .clickable { engage() }
                .size(64.dp)
                .clip(shape)
                .background(ExpressiveRole.containerOf(request.rank)),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = request.label,
                style = expressiveType().step(request.scale)
                    .copy(color = ExpressiveRole.onContainerOf(request.rank)),
            )
        }
    }
}

/**
 * A rectangular [ExpressiveSurface]-shaped tile, [ComposableRequest.label] set at
 * [ExpressiveType.step] of [ComposableRequest.scale] -- [ExpressiveType.bodyMedium] for any scale
 * name [ExpressiveType.step] doesn't recognize, the same fallback every other template in this
 * registry gets -- with [ComposableRequest.subtitle] beneath it at [ExpressiveType.bodyMedium]
 * always. The same title+detail two-line form `conveyance-h2g2`'s `h2g2.tile.record` offers, in
 * M3's own type scale.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TitleTile(request: ComposableRequest) {
    val shape = ExpressiveSurface.shapeOf(request.surface)
    val onContainer = ExpressiveRole.onContainerOf(request.rank)
    val type = expressiveType()
    Offer(act = request.act, modifier = Modifier.wrapContentSize()) {
        Box(
            modifier = Modifier
                .tell(owesTell, weight)
                .clickable { engage() }
                .clip(shape)
                .background(ExpressiveRole.containerOf(request.rank))
                .padding(horizontal = 20.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Column {
                BasicText(text = request.label, style = type.step(request.scale).copy(color = onContainer))
                request.subtitle?.let {
                    BasicText(text = it, style = type.bodyMedium.copy(color = onContainer))
                }
            }
        }
    }
}

/**
 * A [Shape] that morphs between two [androidx.graphics.shapes.RoundedPolygon]s at [progress].
 *
 * Maps the morphed path's own *measured* bounds onto `(0,0)-(size.width,size.height)`, the same
 * way material3's real [androidx.compose.material3.toShape] does it for a single (non-morphing)
 * [androidx.graphics.shapes.RoundedPolygon] -- rather than assuming a fixed coordinate range. A
 * hardcoded "normalized polygons live in [-1,1]" assumption is exactly what broke this class
 * before: every `MaterialShapes` polygon is actually `.normalized()`-ed into `(0,0)-(1,1)`, not
 * `(-1,1)`, so that assumption put the whole shape in this box's bottom-right quadrant only.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
internal class MorphShape(private val morph: Morph, private val progress: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        // Built from the Morph's own cubics via this package's own pathFromCubics rather than
        // material3's toPath() -- material3's toPath() defaults its own Path() when the caller
        // omits *any* parameter (here, startAngle), which off Android resolves to a JVM-stub
        // implementation that throws NotImplementedError; that default is baked into material3's
        // own compiled bytecode and unavoidable through that entry point.
        val path = pathFromCubics(Path(), morph.asCubics(progress))
        val bounds = path.getBounds()
        val scaleX = if (bounds.width > 0f) size.width / bounds.width else 1f
        val scaleY = if (bounds.height > 0f) size.height / bounds.height else 1f
        val matrix = Matrix().apply {
            scale(scaleX, scaleY)
            translate(-bounds.left, -bounds.top)
        }
        path.transform(matrix)
        return Outline.Generic(path)
    }
}

private const val SETTLE_MORPH_MILLIS = 500
private const val INDEFINITE_PULSE_MILLIS = 900

/**
 * An [Offer]-backed control whose clip shape morphs from its resting [ExpressiveSurface] shape
 * toward [ExpressiveSurface.cookie] while the act is
 * [com.hereliesaz.conveyance.ActState.Yielding] -- the only state that carries a live progress
 * value ([com.hereliesaz.conveyance.compose.ActScope.yielding]) -- driven live off that value
 * when it's known. When it's `null` -- work whose end isn't known -- the deformation is rhythmic
 * instead: a continuous, self-driven pulse between the resting and busy shape, so an indefinite
 * wait still visibly reads as "something is happening" rather than sitting on the plain resting
 * shape indistinguishable from `Ready`. Once [com.hereliesaz.conveyance.ActState.Settled], a
 * *third*, independent morph takes over: from the fully busy shape (not wherever the live/pulsed
 * yield progress happened to be an instant before settling -- `Settled` carries no progress value
 * of its own to interpolate from) toward [ExpressiveSurface.heart], animated smoothly over
 * [SETTLE_MORPH_MILLIS] rather than snapped. Snaps back to the plain resting shape at any other
 * state. This is chrome reacting to the framework's own exposed state, not a replacement for the
 * framework's motion: the `position`/`opacity`/etc. [com.hereliesaz.conveyance.Signature] still
 * belongs to Conveyance; only the *shape* belongs to this template.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MorphControl(request: ComposableRequest) {
    val rest = ExpressiveSurface.byName(request.surface)
    val busy = ExpressiveSurface.cookie
    val resolved = ExpressiveSurface.heart
    val busyMorph = remember(rest, busy) { Morph(rest, busy) }
    val settleMorph = remember(busy, resolved) { Morph(busy, resolved) }
    val pulseTransition = rememberInfiniteTransition(label = "morph-pulse")
    val pulse by pulseTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(INDEFINITE_PULSE_MILLIS, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )
    Offer(act = request.act) {
        val settleProgress = remember { Animatable(0f) }
        LaunchedEffect(state) {
            if (state is ActState.Settled) {
                settleProgress.animateTo(1f, tween(SETTLE_MORPH_MILLIS))
            } else {
                settleProgress.snapTo(0f)
            }
        }
        val shape = if (settleProgress.value > 0f) {
            MorphShape(settleMorph, settleProgress.value)
        } else {
            // 0f (plain rest shape) at any state that isn't Yielding, matching the doc's "snaps
            // back to the plain resting shape at any other state" -- `pulse` only ever substitutes
            // for a genuinely Yielding-but-unknown-extent act, never for Ready/Blocked.
            val busyProgress = (state as? ActState.Yielding)?.let { it.extent ?: pulse } ?: 0f
            MorphShape(busyMorph, busyProgress)
        }
        Box(
            modifier = Modifier
                .tell(owesTell, weight)
                .clickable { engage() }
                .size(64.dp)
                .clip(shape)
                .background(ExpressiveRole.containerOf(request.rank)),
            contentAlignment = Alignment.Center,
        ) {
            BasicText(
                text = request.label,
                style = expressiveType().step(request.scale)
                    .copy(color = ExpressiveRole.onContainerOf(request.rank)),
            )
        }
    }
}

private val PRIMARY_SIZE = 64.dp
private val ACCENT_SIZE = 40.dp

/** The accent's own top-left, chosen so its *center* lands on the primary shape's bottom-right
 *  corner: half of it sits under the primary (the "peeking from behind" read), half genuinely
 *  extends past the primary's own footprint -- not fully contained inside it. */
private val ACCENT_OFFSET = PRIMARY_SIZE - ACCENT_SIZE / 2
private val COMPOUND_SIZE = ACCENT_OFFSET + ACCENT_SIZE

/**
 * [containerOf][ExpressiveRole.containerOf]/[onContainerOf][ExpressiveRole.onContainerOf]
 * recognize exactly three ranks and fall back to `"secondary"` for anything else -- reproduced
 * here so [accentRankFor]'s own rotation is applied to the *same* effective bucket
 * [ExpressiveRole.containerOf] will actually resolve, rather than to the raw manifest string. An
 * out-of-vocabulary `rank` string still needs the accent to land on a role distinct from the
 * primary's, and the primary's own out-of-vocabulary role is always `secondaryContainer`.
 */
internal fun normalizedRank(rank: String): String = when (rank) {
    "primary" -> "primary"
    "tertiary" -> "tertiary"
    else -> "secondary"
}

/** The rank a [CompoundBadge]'s accent shape borrows its color from -- a true 3-cycle over
 *  [normalizedRank]'s three buckets, so the accent's resolved container always differs from the
 *  primary's own, for every possible `rank` string including an out-of-vocabulary one. */
internal fun accentRankFor(rank: String): String = when (normalizedRank(rank)) {
    "primary" -> "tertiary"
    "tertiary" -> "secondary"
    else -> "primary"
}

/**
 * A compound badge: a smaller accent [ExpressiveSurface] polygon peeking from behind the primary
 * shape -- its own center offset onto the primary shape's bottom-right corner, so half of it sits
 * hidden under the primary and half genuinely extends past the primary's own footprint, drawn in
 * a different [ExpressiveRole] than the primary shape's own -- the layered-shape composition M3
 * Expressive's own reference material uses rather than a single polygon standing alone. The
 * accent is always [ExpressiveSurface.burst] (or [ExpressiveSurface.spark] when the primary shape
 * *is* `burst`, so the two are never identical) -- a fixed choice, not selectable per element,
 * since nothing in the manifest's `surface`/`hue`/`scale` vocabulary names a second shape.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CompoundBadge(request: ComposableRequest) {
    val primaryShape = ExpressiveSurface.shapeOf(request.surface)
    val accentPolygon = if (request.surface == "burst") ExpressiveSurface.spark else ExpressiveSurface.burst
    val accentShape = RoundedPolygonShape(accentPolygon)
    val accentRank = accentRankFor(request.rank)

    Offer(act = request.act) {
        Box(
            modifier = Modifier
                .tell(owesTell, weight)
                .clickable { engage() }
                .size(COMPOUND_SIZE),
            contentAlignment = Alignment.TopStart,
        ) {
            Box(
                modifier = Modifier
                    .padding(start = ACCENT_OFFSET, top = ACCENT_OFFSET)
                    .size(ACCENT_SIZE)
                    .clip(accentShape)
                    .background(ExpressiveRole.containerOf(accentRank)),
            )
            Box(
                modifier = Modifier
                    .size(PRIMARY_SIZE)
                    .clip(primaryShape)
                    .background(ExpressiveRole.containerOf(request.rank)),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = request.label,
                    style = expressiveType().step(request.scale)
                        .copy(color = ExpressiveRole.onContainerOf(request.rank)),
                )
            }
        }
    }
}
