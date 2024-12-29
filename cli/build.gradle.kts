plugins {
    kotlin("jvm")
    application
}

application {
    mainClass.set("com.jdamcd.arrivals.cli.CliKt")
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.koin)
    implementation(libs.kotlin.coroutines)
    implementation(libs.clikt)
}
