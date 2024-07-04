plugins {
    java
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "com.jtprince.silksigns"
version = "1.0-SNAPSHOT"

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
            expand("version" to version)
        }
    }

    shadowJar {
        isEnableRelocation = true
        relocationPrefix = "${project.group}.lib"
        minimize()
    }
}
