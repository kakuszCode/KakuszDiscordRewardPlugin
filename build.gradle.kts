plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.serialization") version "1.7.22"
}
group = "pl.kakuszcode"
version = "1.7.0"

allprojects {
    apply(plugin = "java")
    apply(plugin = "org.jetbrains.kotlin.plugin.serialization")
    apply(plugin = "kotlin")
    apply(plugin = "com.github.johnrengelman.shadow")

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
        maven { url = uri("https://storehouse.okaeri.eu/repository/maven-public/") }
        maven {
            url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
        }
    }
    dependencies {
        implementation("eu.okaeri:okaeri-configs-yaml-bukkit:4.0.8")
        compileOnly("io.papermc.paper:paper-api:1.19.3-R0.1-SNAPSHOT")
        implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.10")
        implementation("eu.okaeri:okaeri-configs-serdes-bukkit:4.0.8")
        implementation("com.github.ben-manes.caffeine:caffeine:2.9.3")
        implementation("com.h2database:h2:2.1.214")
        implementation("org.postgresql:postgresql:42.5.1")
        compileOnly("me.clip:placeholderapi:2.11.2")
        implementation("com.zaxxer:HikariCP:4.0.3")
        implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.1")
        implementation("io.ktor:ktor-client-core:2.2.1")
        implementation("io.ktor:ktor-client-cio:2.2.1")
        implementation("io.ktor:ktor-client-content-negotiation:2.3.9")
        implementation("io.ktor:ktor-client-websockets:2.2.1")
        implementation("net.dv8tion:JDA:5.0.0-beta.2")
    }
    tasks.withType<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar> {
        val prefix = "pl.kakuszcode.discordreward.libs"
        listOf(
            "eu.okaeri",
            "io.ktor",
            "com.squareup",
            "okio",
            "com.github.ben-manes",
            "com.zaxxer",
            "net.dv8tion"
        ).forEach { pack ->
            relocate(pack, "$prefix.$pack")
        }
    }
}
subprojects {
    apply(plugin = "maven-publish")
    configure<PublishingExtension> {
        repositories {
            maven {
                name = "GitHubPackages"
                url = uri("https://maven.pkg.github.com/kakuszCode/KakuszDiscordRewardPlugin")
                credentials {
                    username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                    password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
                }
            }
        }
        publications {
            register<MavenPublication>("gpr") {
                from(components["java"])
            }
        }
    }
}
