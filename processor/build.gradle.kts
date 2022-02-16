repositories.mavenCentral()

plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    // todo
}

tasks.getByName<JavaCompile>("compileJava") {
    targetCompatibility = "1.8"
}

tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
