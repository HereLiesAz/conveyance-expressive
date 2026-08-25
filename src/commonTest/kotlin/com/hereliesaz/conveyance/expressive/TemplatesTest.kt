package com.hereliesaz.conveyance.expressive

import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.graphics.shapes.Morph
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
class TemplatesTest {

    private val density = Density(1f)

    /**
     * The exact defect a GLEE audit found this session: [MorphShape] assumed a morphed polygon's
     * coordinates live in `[-1,1]`, but every `MaterialShapes` polygon is `.normalized()`-ed into
     * `[0,1]` -- the old code put the whole shape in the clip box's bottom-right quadrant only.
     * Verified here at several progress values, not just the rest/settled endpoints, since the
     * bug was in the coordinate mapping itself, not anything progress-dependent.
     */
    @Test
    fun `MorphShape maps the morphed path onto the full target box, not one quadrant`() {
        val morph = Morph(ExpressiveSurface.circle, ExpressiveSurface.cookie9Sided)
        val size = Size(64f, 64f)
        listOf(0f, 0.25f, 0.5f, 0.75f, 1f).forEach { progress ->
            val outline = MorphShape(morph, progress).createOutline(size, LayoutDirection.Ltr, density)
            val bounds = (outline as Outline.Generic).path.getBounds()
            assertTrue(bounds.left < size.width * 0.15f, "left=${bounds.left} at progress=$progress")
            assertTrue(bounds.top < size.height * 0.15f, "top=${bounds.top} at progress=$progress")
            assertTrue(bounds.right > size.width * 0.85f, "right=${bounds.right} at progress=$progress")
            assertTrue(bounds.bottom > size.height * 0.85f, "bottom=${bounds.bottom} at progress=$progress")
        }
    }

    @Test
    fun `accentRankFor is a true 3-cycle over the three real ranks`() {
        assertEquals("tertiary", accentRankFor("primary"))
        assertEquals("secondary", accentRankFor("tertiary"))
        assertEquals("primary", accentRankFor("secondary"))
    }

    /**
     * The exact collision a GLEE audit found this session: [accentRankFor]'s own fallback and
     * [ExpressiveRole.containerOf]'s fallback both used to collapse to `"secondary"` for the same
     * out-of-vocabulary rank string, so a compound badge's accent could silently render in the
     * identical color as its primary shape.
     */
    @Test
    fun `accentRankFor never collides with containerOf's own out-of-vocabulary fallback`() {
        listOf("primary", "secondary", "tertiary", "", "quaternary", "PRIMARY", "nonsense").forEach { rank ->
            val primaryColor = ExpressiveRole.containerOf(rank)
            val accentColor = ExpressiveRole.containerOf(accentRankFor(rank))
            assertNotEquals(
                primaryColor, accentColor,
                "rank=\"$rank\": primary and accent must never resolve to the identical container.",
            )
        }
    }

    @Test
    fun `normalizedRank collapses any unrecognized string to secondary, same as containerOf`() {
        assertEquals("primary", normalizedRank("primary"))
        assertEquals("tertiary", normalizedRank("tertiary"))
        assertEquals("secondary", normalizedRank("secondary"))
        assertEquals("secondary", normalizedRank(""))
        assertEquals("secondary", normalizedRank("quaternary"))
    }
}
