plugins {
    kotlin("multiplatform").version("1.7.10").apply(false)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
