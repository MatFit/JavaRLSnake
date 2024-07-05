// Gradle build script to get all the
// dependancies and that written in Kotlin DSL -> alternative to Groovy
// DSL

plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    // Logback dependency
    implementation("org.slf4j:slf4j-api:1.7.32")
    implementation("ch.qos.logback:logback-classic:1.2.6")

    // Deeplearning4j dependencies
    implementation("org.deeplearning4j:deeplearning4j-nn:1.0.0-beta7")
    implementation("org.deeplearning4j:rl4j:1.0.0-beta7")
    implementation("org.deeplearning4j:rl4j-api:1.0.0-beta7")
    implementation("org.deeplearning4j:rl4j-core:1.0.0-beta7")

    // ND4J dependencies
    implementation("org.nd4j:nd4j-backends:1.0.0-beta7")
    implementation("org.nd4j:nd4j-cuda-10.1:1.0.0-beta7")
    implementation("org.nd4j:nd4j-native-platform:1.0.0-beta7")
}

tasks.test {
    useJUnitPlatform()
}