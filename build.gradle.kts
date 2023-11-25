plugins {
    kotlin("multiplatform").version("1.9.20").apply(false)
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.9.20")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.20")
        classpath("com.codingfeline.buildkonfig:buildkonfig-gradle-plugin:0.15.0")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
