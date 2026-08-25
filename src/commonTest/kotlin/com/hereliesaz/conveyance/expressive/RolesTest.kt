package com.hereliesaz.conveyance.expressive

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RolesTest {

    @Test
    fun `containerOf resolves the three real ranks to their own container`() {
        assertEquals(ExpressiveRole.primaryContainer, ExpressiveRole.containerOf("primary"))
        assertEquals(ExpressiveRole.secondaryContainer, ExpressiveRole.containerOf("secondary"))
        assertEquals(ExpressiveRole.tertiaryContainer, ExpressiveRole.containerOf("tertiary"))
    }

    @Test
    fun `containerOf falls back to secondary for an out-of-vocabulary rank`() {
        assertEquals(ExpressiveRole.secondaryContainer, ExpressiveRole.containerOf(""))
        assertEquals(ExpressiveRole.secondaryContainer, ExpressiveRole.containerOf("quaternary"))
    }

    @Test
    fun `onContainerOf mirrors containerOf's own three-way split`() {
        assertEquals(ExpressiveRole.onPrimaryContainer, ExpressiveRole.onContainerOf("primary"))
        assertEquals(ExpressiveRole.onSecondaryContainer, ExpressiveRole.onContainerOf("secondary"))
        assertEquals(ExpressiveRole.onTertiaryContainer, ExpressiveRole.onContainerOf("tertiary"))
        assertEquals(ExpressiveRole.onSecondaryContainer, ExpressiveRole.onContainerOf("nonsense"))
    }

    @Test
    fun `the six colors are genuinely distinct`() {
        val colors = listOf(
            ExpressiveRole.primaryContainer, ExpressiveRole.onPrimaryContainer,
            ExpressiveRole.secondaryContainer, ExpressiveRole.onSecondaryContainer,
            ExpressiveRole.tertiaryContainer, ExpressiveRole.onTertiaryContainer,
        )
        colors.forEachIndexed { i, a ->
            colors.forEachIndexed { j, b ->
                if (i != j) assertNotEquals(a, b, "Colors at $i and $j should not collapse.")
            }
        }
    }
}
