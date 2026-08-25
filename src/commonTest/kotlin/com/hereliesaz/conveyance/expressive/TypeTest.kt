package com.hereliesaz.conveyance.expressive

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TypeTest {

    @Test
    fun `step resolves every real M3 role name to its own step`() {
        val type = expressiveType()
        assertEquals(type.displayLarge, type.step("displayLarge"))
        assertEquals(type.displayMedium, type.step("displayMedium"))
        assertEquals(type.displaySmall, type.step("displaySmall"))
        assertEquals(type.headlineLarge, type.step("headlineLarge"))
        assertEquals(type.headlineMedium, type.step("headlineMedium"))
        assertEquals(type.headlineSmall, type.step("headlineSmall"))
        assertEquals(type.titleLarge, type.step("titleLarge"))
        assertEquals(type.titleMedium, type.step("titleMedium"))
        assertEquals(type.titleSmall, type.step("titleSmall"))
        assertEquals(type.bodyLarge, type.step("bodyLarge"))
        assertEquals(type.bodyMedium, type.step("bodyMedium"))
        assertEquals(type.bodySmall, type.step("bodySmall"))
        assertEquals(type.labelLarge, type.step("labelLarge"))
        assertEquals(type.labelMedium, type.step("labelMedium"))
        assertEquals(type.labelSmall, type.step("labelSmall"))
    }

    @Test
    fun `step also accepts h2g2-style aliases`() {
        val type = expressiveType()
        assertEquals(type.displayMedium, type.step("hero"))
        assertEquals(type.headlineLarge, type.step("section"))
        assertEquals(type.titleLarge, type.step("lead"))
        assertEquals(type.bodyMedium, type.step("body"))
        assertEquals(type.titleSmall, type.step("capsule"))
        assertEquals(type.labelLarge, type.step("eyebrow"))
        assertEquals(type.labelMedium, type.step("endCap"))
        assertEquals(type.labelSmall, type.step("micro"))
    }

    @Test
    fun `step falls back to bodyMedium for an unrecognized scale name`() {
        val type = expressiveType()
        assertEquals(type.bodyMedium, type.step(""))
        assertEquals(type.bodyMedium, type.step("titleMedium2"))
    }

    /**
     * All fifteen steps are distinct except one: `titleSmall` and `labelLarge` are, verified
     * against the pinned material3 1.5.0-alpha26 token tables (`TypeScaleTokens.kt`), genuinely
     * defined with identical numbers -- 14sp/Medium/20sp line height/0.1sp tracking -- for both
     * roles. That is the real M3 spec, not a transcription slip, so the pair is named and excluded
     * here rather than asserted apart.
     */
    @Test
    fun `the fifteen steps are distinct, except titleSmall and labelLarge which the M3 spec defines identically`() {
        val type = expressiveType()
        val named = listOf(
            "displayLarge" to type.displayLarge, "displayMedium" to type.displayMedium, "displaySmall" to type.displaySmall,
            "headlineLarge" to type.headlineLarge, "headlineMedium" to type.headlineMedium, "headlineSmall" to type.headlineSmall,
            "titleLarge" to type.titleLarge, "titleMedium" to type.titleMedium, "titleSmall" to type.titleSmall,
            "bodyLarge" to type.bodyLarge, "bodyMedium" to type.bodyMedium, "bodySmall" to type.bodySmall,
            "labelLarge" to type.labelLarge, "labelMedium" to type.labelMedium, "labelSmall" to type.labelSmall,
        )
        val knownCollision = setOf("titleSmall", "labelLarge")
        named.forEachIndexed { i, (nameA, a) ->
            named.forEachIndexed { j, (nameB, b) ->
                if (i != j && setOf(nameA, nameB) != knownCollision) {
                    assertNotEquals(a, b, "Steps $nameA and $nameB should not collapse to the same style.")
                }
            }
        }
        assertEquals(type.titleSmall, type.labelLarge)
    }

    /**
     * The exact three values a GLEE audit found mistranscribed this session, against the pinned
     * material3 1.5.0-alpha26 M3 Expressive token revision -- corrected then, pinned here so a
     * future edit can't silently drift back to the older "classic" M3 numbers.
     */
    @Test
    fun `the three token-revision letter-spacing values match the pinned material3 version`() {
        val type = expressiveType()
        assertEquals(-0.2f, type.displayLarge.letterSpacing.value)
        assertEquals(0.2f, type.titleMedium.letterSpacing.value)
        assertEquals(0.2f, type.bodyMedium.letterSpacing.value)
    }
}
