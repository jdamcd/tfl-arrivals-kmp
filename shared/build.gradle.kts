import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.codingfeline.buildkonfig")
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
        val ktorVersion = "2.2.1"

        commonMain.dependencies {
            implementation("io.ktor:ktor-client-core:${ktorVersion}")
            implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
            implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
            implementation("io.ktor:ktor-client-logging:${ktorVersion}")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4-native-mt")
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }

        macosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:${ktorVersion}")
        }
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
