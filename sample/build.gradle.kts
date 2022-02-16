repositories.mavenCentral()

plugins {
    id("application")
    id("org.jetbrains.kotlin.jvm")
}

application {
    mainClass.set("test.sample.AppKt")
}

dependencies {
    implementation(project(":processor"))
}

tasks.getByName<JavaCompile>("compileJava") {
    targetCompatibility = "1.8"
}

tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}
