plugins {
    kotlin("jvm") version "2.4.20"
    id("com.gradleup.shadow") version "9.6.1"

    antlr

    application
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

repositories {
    mavenCentral()
}

dependencies {
    // Use RxKotlin
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    // Use Logger
    implementation("ch.qos.logback:logback-classic:1.6.3")

    // Use JDT
    implementation("org.eclipse.jdt:org.eclipse.jdt.core:3.47.0")

    // Use the Kotlin test library.
    testImplementation("org.jetbrains.kotlin:kotlin-test")

    // Use the Kotlin JUnit integration.
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")

    // Use ANTLR
    antlr("org.antlr:antlr4:4.13.2")
}

application {
    mainClass.set("jp.ac.osaka_u.sdl.nil.NILMainKt")
}

tasks.generateGrammarSource {
    // Comment out when generating lexers and parsers from g4 file
    enabled = false
    outputDirectory = file("$projectDir/src/main/java")
}

tasks.generateTestGrammarSource {
    enabled = false
}
