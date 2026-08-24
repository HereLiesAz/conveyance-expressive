package com.hereliesaz.conveyance.expressive

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.toPath
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.Morph
import com.hereliesaz.conveyance.Act
import com.hereliesaz.conveyance.ActState
import com.hereliesaz.conveyance.compose.Offer

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
 * A rectangular [ExpressiveSurface]-shaped tile, [ComposableRequest.label] set at [titleMedium]
 * (or [ComposableRequest.scale] if given a real type-role name) with [ComposableRequest.subtitle]
 * beneath it at [bodyMedium] -- the same title+detail two-line form `conveyance-h2g2`'s
 * `h2g2.tile.record` offers, in M3's own type scale.
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

/** A [Shape] that morphs between two [androidx.graphics.shapes.RoundedPolygon]s at [progress]. */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
private class MorphShape(private val morph: Morph, private val progress: Float) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = morph.toPath(progress = progress)
        val matrix = Matrix().apply {
            scale(size.width / 2f, size.height / 2f)
            translate(1f, 1f)
        }
        path.transform(matrix)
        return Outline.Generic(path)
    }
}

private const val SETTLE_MORPH_MILLIS = 500

/**
 * An [Offer]-backed control whose clip shape morphs from its resting [ExpressiveSurface] shape
 * toward [ExpressiveSurface.cookie] while the act is
 * [com.hereliesaz.conveyance.ActState.Yielding] -- the only state that carries a live progress
 * value ([com.hereliesaz.conveyance.compose.ActScope.yielding]) -- driven live off that value, no
 * timer of its own. Once [com.hereliesaz.conveyance.ActState.Settled], a *second*, independent
 * morph takes over: from the fully busy shape (not wherever the live yield progress happened to
 * be an instant before settling -- `Settled` carries no progress value of its own to interpolate
 * from) toward [ExpressiveSurface.heart], animated smoothly over [SETTLE_MORPH_MILLIS] rather
 * than snapped. Snaps back to the plain resting shape at any other state. This is chrome reacting to the framework's own exposed state, not a replacement for the
 * framework's motion: the *position*/opacity/etc. [com.hereliesaz.conveyance.Signature] still
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
            MorphShape(busyMorph, yielding ?: 0f)
        }
        Box(
            modifier = Modifier
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

private val ACCENT_OFFSET = 22.dp
private val ACCENT_SIZE = 40.dp
private val PRIMARY_SIZE = 64.dp

/** The rank a [CompoundBadge]'s accent shape borrows its color from -- a fixed rotation so the accent always reads as a distinct role from the primary shape's own [ExpressiveRole.containerOf]. */
private fun accentRankFor(rank: String): String = when (rank) {
    "primary" -> "tertiary"
    "secondary" -> "primary"
    else -> "secondary"
}

/**
 * A compound badge: a smaller accent [ExpressiveSurface] polygon peeking from behind the primary
 * shape, offset toward its bottom-right corner and drawn in a different [ExpressiveRole] than the
 * primary shape's own -- the layered-shape composition M3 Expressive's own reference material
 * uses rather than a single polygon standing alone. The accent is always
 * [ExpressiveSurface.burst] (or [ExpressiveSurface.spark] when the primary shape *is* `burst`,
 * so the two are never identical) -- a fixed choice, not selectable per element, since nothing in
 * the manifest's `surface`/`hue`/`scale` vocabulary names a second shape.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CompoundBadge(request: ComposableRequest) {
    val primaryShape = ExpressiveSurface.shapeOf(request.surface)
    val accentPolygon = if (request.surface == "burst") ExpressiveSurface.spark else ExpressiveSurface.burst
    val accentShape = accentPolygon.toShape()
    val accentRank = accentRankFor(request.rank)

    Offer(act = request.act) {
        Box(
            modifier = Modifier.size(PRIMARY_SIZE + ACCENT_OFFSET),
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
