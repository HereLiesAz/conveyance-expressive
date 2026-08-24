# conveyance-expressive

A composable-set library for [Conveyance](https://github.com/HereLiesAz/Conveyance): Material 3 Expressive-styled templates -- shape-morph, motion, and token vocabulary drawn from M3 Expressive, reimplemented as Conveyance-native composables.

## What this is

Per [azphalt's `spec/composable.md`](https://github.com/HereLiesAz/azphalt/blob/main/spec/composable.md),
a `kind: "composable"` `.azp` package is a **pure header**: it names this artifact's Gradle
coordinates (`library.group` / `library.artifact`) and selects a `templateId`, `hue`,
`surface`, `scale`, and `act` from it. It carries no code of its own. This repository *is* the
artifact a composable package's `library` block points at -- the `.azp` package itself is
authored and published separately, wherever its author chooses; this repo does not need to hold
one.

Example composable manifest referencing this library:

```jsonc
{
  "azphalt": "0.1",
  "id": "com.hereliesaz.azphalt.example",
  "name": "Example",
  "version": "1.0.0",
  "kind": "composable",
  "license": "MIT",
  "compat": ">=0.1",
  "composable": {
    "library": { "group": "com.hereliesaz.conveyance", "artifact": "conveyance-expressive", "version": "0.1.0" },
    "elements": [
      { "id": "confirm-record", "templateId": "expressive.control.morph", "hue": "primary", "surface": "pill", "scale": "lead", "act": "create", "jobs": ["confirms a destructive action"] }
    ]
  },
  "files": {}
}
```

## What's here

- **`ExpressiveSurface`** (`Shapes.kt`) -- a curated, named subset of M3 Expressive's 35
  `MaterialShapes` polygons (`badge`, `pill`, `bloom`, `spark`, `burst`, `gem`, `arch`, `cookie`).
  `MaterialShapes`/`Morph`/`RoundedPolygon` (`androidx.graphics:graphics-shapes`) have been
  commonMain-safe since 1.1.0 of that artifact, so this is a real KMP dependency, not
  Android-only, unlike `conveyance-compose`'s own more conservative androidMain-only wiring of
  the same material3 version.
- **`ExpressiveRole`** (`Roles.kt`) -- M3's primary/secondary/tertiary container color roles.
  Chosen deliberately over h2g2's per-entity `hueOf` hashing: M3 Expressive's whole color-role
  model already lines up with Conveyance's own `Rank` (`Channel.Hue` carries
  `Meaning.SemanticRank`), so this composable-set's `hue` manifest field selects a *rank*, not a
  hashed identity.
- **`Templates`** (`Templates.kt`) -- the `templateId` registry. Two templates so far:
  `expressive.badge.shape` (a static polygon badge) and `expressive.control.morph` -- an
  `Offer`-backed control whose clip shape morphs from its resting polygon toward a busier one
  (`cookie`) while the act is `ActState.Yielding`, driven by `ActScope.yielding`'s own live
  progress value, and snaps back at rest. The shape reacts to the framework's own exposed state;
  the underlying `Signature` (position, displacement, residue) stays entirely Conveyance's.

## Status

A first real slice, not a finished set. Two templates exist against eight named shapes and three
color roles; M3 Expressive's actual range (its type scale, its full 35-shape vocabulary, more
motion-reactive shapes beyond the one Yield-driven morph) is much wider than that.

## Using it

```kotlin
repositories {
    maven("https://jitpack.io")
}
dependencies {
    implementation("com.github.HereLiesAz:conveyance-expressive:main-SNAPSHOT")
}
```

Resolved via [JitPack](https://jitpack.io) directly from this repository -- `conveyance-core` and
`conveyance-compose` both apply `maven-publish`, which is all JitPack needs, so there is no
separate publish step to configure. Conveyance itself has no tagged release yet, so this artifact
and its upstream dependency on Conveyance both pin to `main-SNAPSHOT` for now; switch both to a
real tag once one exists.
