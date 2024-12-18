import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.buildkonfig)
    alias(libs.plugins.wire)
    alias(libs.plugins.ksp)
    alias(libs.plugins.mockmp)
}

kotlin {
    listOf(
        macosArm64(),
        macosX64()
    ).forEach {
        it.binaries.framework {
            baseName = "TflArrivals"
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.bundles.ktor.common)
            implementation(libs.kotlin.datetime)
            implementation(libs.kotlin.coroutines)
            implementation(libs.wire.runtime)
            implementation(libs.okio)
            implementation(libs.koin)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
        }

        macosMain.dependencies {
            implementation(libs.ktor.client.macos)
        }
    }
}

mockmp {
    onTest {
        withHelper()
    }
}

wire {
    kotlin {}
    sourcePath {
        srcDir("src/commonMain/proto")
    }
}

@Suppress("TooGenericExceptionCaught")
configure<BuildKonfigExtension> {
    packageName = "com.jdamcd.tflarrivals"

    val props = Properties()
    try {
        props.load(file("secret.properties").inputStream())
    } catch (_: Exception) {}

    defaultConfigs {
        buildConfigField(
            FieldSpec.Type.STRING,
            "TFL_APP_KEY",
            props["tfl_app_key"]?.toString() ?: ""
        )
    }
}
