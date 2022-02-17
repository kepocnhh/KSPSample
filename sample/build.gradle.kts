repositories.mavenCentral()

plugins {
    id("application")
    id("org.jetbrains.kotlin.jvm")
    id("com.google.devtools.ksp") version "1.5.31-1.0.1"
}

application {
    mainClass.set("test.sample.AppKt")
}

dependencies {
    implementation(project(":processor"))
    ksp(project(":processor"))
}

kotlin.sourceSets["main"].kotlin.srcDir(File(buildDir, "generated/ksp/main/kotlin"))

tasks.getByName<JavaCompile>("compileJava") {
    targetCompatibility = "1.8"
}

tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    kotlinOptions.jvmTarget = "1.8"
}
