plugins {
    kotlin("jvm") version "1.5.30"
    id("org.jlleitschuh.gradle.ktlint") version "10.1.0"
    id("com.palantir.docker-compose") version "0.28.0"
    id("com.palantir.git-version") version "0.12.3"
    id("maven-publish")
    id("com.jfrog.artifactory") version "4.24.16"
}

val gitVersion: groovy.lang.Closure<String> by extra

group = "codes.laurence.statsd4k"
version = gitVersion().replace(".dirty", "")

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

    val build by existing

    artifactoryPublish {
        dependsOn(build)
        dependsOn("publishJvmPublicationToMavenLocal")
    }

    test {
        useJUnitPlatform()
    }
}

dockerCompose {
    setDockerComposeFile("$rootDir/docker-compose.yml")
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    disabledRules.set(
        setOf("no-wildcard-imports", "filename")
    )
}

val sourcesJar by tasks.creating(Jar::class) {
    archiveClassifier.set("sources")
    from(sourceSets.getByName("main").allSource)
}

publishing {
    publications {
        create<MavenPublication>("statsd4k") {
            groupId = "codes.laurence.statsd4k"
            artifactId = "statsd4k"
            version = version

            artifact(sourcesJar)
            from(components["java"])
        }
    }
}

artifactory {
    setContextUrl("https://plurex.jfrog.io/artifactory")
    publish(
        delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.PublisherConfig> {
            repository(
                delegateClosureOf<org.jfrog.gradle.plugin.artifactory.dsl.DoubleDelegateWrapper> {
                    setProperty("repoKey", "codes.laurence.statsd4k")
                    setProperty("username", System.getenv("JFROG_USER"))
                    setProperty("password", System.getenv("JFROG_PASSWORD"))
                    setProperty("maven", true)
                }
            )
            defaults(
                delegateClosureOf<org.jfrog.gradle.plugin.artifactory.task.ArtifactoryTask> {
                    publications("statsd4k")
                }
            )
        }
    )
}
