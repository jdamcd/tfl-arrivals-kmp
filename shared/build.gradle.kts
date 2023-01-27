plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
}

kotlin {
    macosArm64("macOS") {
        binaries {
            framework {
                baseName = "TflArrivals"
            }
        }
    }

    sourceSets {
        val ktorVersion = "2.2.1"

        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:${ktorVersion}")
                implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
                implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
                implementation("io.ktor:ktor-client-logging:${ktorVersion}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4-native-mt")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val macOSMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-darwin:${ktorVersion}")
            }
        }
    }
}
