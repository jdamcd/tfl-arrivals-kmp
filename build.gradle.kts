plugins {
    kotlin("multiplatform").version("2.0.21").apply(false)
    id("com.diffplug.spotless") version "6.25.0"
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:2.0.21")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.15.2")
        classpath("com.squareup.wire:wire-gradle-plugin:5.1.0")
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
            ktlint("1.4.1")
        }
    }
}
