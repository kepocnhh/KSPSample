repositories.mavenCentral()

plugins {
    id("org.jetbrains.kotlin.jvm")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.5.31-1.0.1")
    implementation("com.squareup:kotlinpoet:1.10.2")
//    implementation("com.squareup:kotlinpoet-ksp:1.10.2")
}

tasks.getByName<JavaCompile>("compileJava") {
    targetCompatibility = "1.8"
}

tasks.getByName<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
    kotlinOptions.jvmTarget = "1.8"
}
