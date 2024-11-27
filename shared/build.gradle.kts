import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.codingfeline.buildkonfig")
    id("com.squareup.wire")
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
        val ktorVersion = "3.0.1"

        commonMain.dependencies {
            implementation("io.ktor:ktor-client-core:${ktorVersion}")
            implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
            implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
            implementation("io.ktor:ktor-client-logging:${ktorVersion}")
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
            implementation("com.squareup.wire:wire-runtime:5.1.0")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        macosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:${ktorVersion}")
        }
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
