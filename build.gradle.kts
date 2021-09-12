plugins {
    kotlin("jvm") version "1.5.30"
}

group = "codes.laurence.statsd4k"

val ktorVersion: String by project
val mockkVersion: String by project
val junitJupiterVersion: String by project
val assertKVersion: String by project

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.ktor:ktor-network:$ktorVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertKVersion")

}

tasks {
    test {
        useJUnitPlatform()
    }
}