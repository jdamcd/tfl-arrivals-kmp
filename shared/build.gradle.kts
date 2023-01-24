plugins {
    kotlin("multiplatform")
}

kotlin {

    macosArm64("macOS") {
        binaries {
            framework {
                baseName = "TFLArrivals"
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val macOSMain by getting {
            dependencies {
                // Ktor
            }
        }
    }
}
