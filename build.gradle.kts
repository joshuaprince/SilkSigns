plugins {
    java
    id("io.github.goooler.shadow") version "8.1.8"
    id("com.palantir.git-version") version "3.1.0"
}

val gitVersion: groovy.lang.Closure<String> by extra

group = "com.jtprince.silksigns"
version = gitVersion()

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.6-R0.1-SNAPSHOT")
    implementation("de.exlll:configlib-paper:4.5.0")
    implementation("org.bstats:bstats-bukkit:3.0.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    wrapper {
        gradleVersion = "8.8"
        distributionType = Wrapper.DistributionType.ALL
    }

    processResources {
        filesMatching("plugin.yml") {
            expand("version" to version.toString().trimStart('v'))
        }
        inputs.property("version", version) // ensure cache is invalidated after version bumps
    }

    jar {
        archiveAppendix = "Thin"
    }

    shadowJar {
        archiveAppendix = "Paper"
        archiveClassifier = "" // Previously "all"

        isEnableRelocation = true
        relocationPrefix = "${project.group}.lib"
        minimize()
    }

    val buildSnapshot = register<Copy>("buildSnapshot") {
        // Copy the latest artifact from `assemble` task to a consistent place for symlinking into a server.
        dependsOn(shadowJar)
        from(shadowJar)
        into("build/libs")
        rename { "${rootProject.name}-Paper-SNAPSHOT.jar" }
    }

    build {
        dependsOn(shadowJar)
        dependsOn(buildSnapshot)
    }
}
