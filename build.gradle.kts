plugins {
    kotlin("multiplatform").version("1.9.20").apply(false)
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.20")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
