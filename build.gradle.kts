plugins {
    kotlin("multiplatform").version("1.9.21").apply(false)
    id("com.diffplug.spotless") version "6.23.3"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.21")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.21")
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.15.0")
    }
}

allprojects {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    apply(plugin = "com.diffplug.spotless")

    spotless {
        kotlin {
            target("**/*.kt")
            ktlint("1.0.1")
        }
    }
}
