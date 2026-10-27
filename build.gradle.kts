import java.util.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.compose)
    `maven-publish`
}

group = "com.hereliesaz.conveyance"
// Read from version.properties, the same file CI stamps releases from. JitPack overrides both
// group and version from the git ref it builds; this is what a local `publishToMavenLocal` produces.
version = Properties().run {
    file("version.properties").inputStream().use { load(it) }
    listOf("versionMajor", "versionMinor", "versionPatch", "versionBuild")
        .joinToString(".") { getProperty(it).trim() }
}

kotlin {
    jvmToolchain(libs.versions.jvmToolchain.get().toInt())

    androidLibrary {
        namespace = "com.hereliesaz.conveyance.expressive"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
    }
    jvm("desktop")

    sourceSets {
        commonMain.dependencies {
            // Pinned to an exact Conveyance commit JitPack has built (status "ok", all core/compose
            // target publications present). A floating main-SNAPSHOT makes KMP metadata
            // non-reproducible. Swap for a release tag once Conveyance cuts one.
            val conveyanceRevision = "bdc4834313edfe27d74172364fb349896ef5e095"
            api("com.github.HereLiesAz.Conveyance:conveyance-core:$conveyanceRevision")
            api("com.github.HereLiesAz.Conveyance:conveyance-compose:$conveyanceRevision")
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.ui)
            // MaterialShapes/Morph only -- geometry, never M3's ColorScheme or components.
            // Safe in commonMain: graphics-shapes (which Morph/RoundedPolygon come from) has
            // been KMP-friendly since 1.1.0, well before this pinned alpha.
            implementation(libs.androidx.material3)
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.uiTest)
        }
        val desktopTest by getting {
            dependencies {
                // The real Skia bindings for the current OS -- without this, any test touching
                // an actual Path/Canvas (Compose Multiplatform's own real graphics, as opposed
                // to a stub) fails to load the native library rather than running.
                implementation(compose.desktop.currentOs)
            }
        }
    }
}

// Kotlin Multiplatform registers one publication per target on its own; there is nothing to
// create here, only a shared description for whichever one a consumer ends up resolving.
publishing {
    publications.withType<MavenPublication>().configureEach {
        pom {
            name.set("Conveyance Expressive")
            description.set(
                "Material 3 Expressive-styled templates -- shape-morph, motion, and token vocabulary drawn from M3 Expressive, reimplemented as Conveyance-native composables.",
            )
            url.set("https://github.com/HereLiesAz/conveyance-expressive")
            licenses {
                license {
                    name.set("Apache License 2.0")
                    url.set("https://www.apache.org/licenses/LICENSE-2.0")
                }
            }
        }
    }
}
