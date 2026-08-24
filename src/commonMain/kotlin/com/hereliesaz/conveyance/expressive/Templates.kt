package com.hereliesaz.conveyance.expressive

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.toPath
import androidx.compose.runtime.Composable
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

/**
 * An [Offer]-backed control whose clip shape morphs from its resting [ExpressiveSurface] shape
 * toward [ExpressiveSurface.cookie] while the act is
 * [com.hereliesaz.conveyance.ActState.Yielding] -- the only state that carries a live progress
 * value ([com.hereliesaz.conveyance.compose.ActScope.yielding]) -- and snaps back to rest
 * otherwise. This is chrome reacting to the framework's own exposed state, not a replacement for
 * the framework's motion: the *position*/opacity/etc. [com.hereliesaz.conveyance.Signature] still
 * belongs to Conveyance; only the *shape* belongs to this template.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun MorphControl(request: ComposableRequest) {
    val rest = ExpressiveSurface.byName(request.surface)
    val busy = ExpressiveSurface.cookie
    val morph = remember(rest, busy) { Morph(rest, busy) }
    Offer(act = request.act) {
        val progress = yielding ?: 0f
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(MorphShape(morph, progress))
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
