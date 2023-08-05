plugins {
    kotlin("jvm") version "1.7.10"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("com.palantir.docker-compose") version "0.34.0"
    id("com.palantir.git-version") version "0.12.3"
    `maven-publish`
    signing
}

val gitVersion: groovy.lang.Closure<String> by extra

group = "codes.laurence.statsd4k"
version = gitVersion().replace(".dirty", "")

val ktorVersion: String by project
val mockkVersion: String by project
val junitJupiterVersion: String by project
val assertKVersion: String by project

val signingKey: Provider<String> =
    providers.environmentVariable("SONATYPE_SIGNING_KEY")
val signingPassword: Provider<String> =
    providers.environmentVariable("SONATYPE_SIGNING_PASSWORD")
val sonatypeUser: Provider<String> =
    providers.environmentVariable("SONATYPE_USER")
val sonatypePassword: Provider<String> =
    providers.environmentVariable("SONATYPE_PASSWORD")

repositories {
    mavenCentral()
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(kotlin("stdlib"))
    api("io.ktor:ktor-network:$ktorVersion")

    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.willowtreeapps.assertk:assertk-jvm:$assertKVersion")
}

tasks {
    test {
        useJUnitPlatform()
    }
    check.configure {
        dependsOn(ktlintCheck)
    }

    withType<AbstractPublishToMaven>().configureEach {
        dependsOn(withType<Sign>())
        mustRunAfter(withType<Sign>())

        doLast {
            logger.lifecycle("[${this.name}] ${project.group}:${project.name}:${project.version}")
        }
    }
}

afterEvaluate {
    // Register signatures afterEvaluate, otherwise the signing plugin creates the signing tasks
    // too early, before all the publications are added.

    if (listOf(signingKey.isPresent, signingPassword.isPresent).all { it }) {
        println("Creating signing task")
        publishing.publications.withType<MavenPublication>().all {
            signing.sign(this)
            logger.lifecycle("configuring signature for publication ${this.name}")
        }
    } else {
        println("No signing task ${signingKey.isPresent} ${signingPassword.isPresent}")
    }
}

publishing {
    repositories {
        if (listOf(sonatypeUser, sonatypePassword).all { it.isPresent }) {
            maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
                name = "sonatype"
                credentials {
                    username = sonatypeUser.get()
                    password = sonatypePassword.get()
                }
            }
        } else {
            // publish to local dir, for testing
            maven(rootProject.layout.buildDirectory.dir("maven-internal")) {
                name = "LocalProjectDir"
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                // Note: Gradle will automatically set the POM 'group' and 'artifactId' from the subproject group and name
                name.set("statsd4k")
                description.set("A composable Kotlin StatsD client")
                url.set("https://github.com/lgwillmore/statsd4k")

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/mit-license")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("lgwillmore")
                        name.set("Laurence Willmore")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/lgwillmore/statsd4k.git")
                    developerConnection.set("scm:git:ssh://github.com/lgwillmore/statsd4k.git")
                    url.set("https://github.com/lgwillmore/statsd4k")
                }

                issueManagement {
                    url.set("https://github.com/lgwillmore/statsd4k/issues")
                }
            }
        }
    }
}

signing {
    if (signingKey.isPresent && signingPassword.isPresent) {
        println("Using in-memory PGP keys")
        useInMemoryPgpKeys(signingKey.get(), signingPassword.get())
    } else {
        println("Using GPG command")
        useGpgCmd()
    }
}

kotlin {
    jvmToolchain {
        (this as JavaToolchainSpec).languageVersion.set(JavaLanguageVersion.of("8"))
    }
}

ktlint {
    disabledRules.set(
        setOf("no-wildcard-imports", "filename")
    )
}

dockerCompose {
    setDockerComposeFile("$rootDir/docker-compose.yml")
}

java {
    withSourcesJar()
    withJavadocJar()
}
