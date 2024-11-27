plugins {
    alias(libs.plugins.spotless)
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.wire) apply false
}

allprojects {
    apply {
        plugin(rootProject.libs.plugins.spotless.get().pluginId)
    }
    spotless {
        kotlin {
            target("**/*.kt")
            ktlint(libs.versions.ktlint.get())
        }
    }
}
