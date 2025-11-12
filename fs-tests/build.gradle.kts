plugins {
  `java-library`
  id("me.champeau.jmh")
  id("fs")
}

description = "Tests for fs."

dependencies {
  jmh(platform(project(":fs-dependencies")))
  jmh(project(":fs-annotations"))
  jmh(project(":fs-core"))
  jmh(project(":fs-internal"))
  jmh("org.openjdk.jmh:jmh-generator-annprocess")
}

java {
  toolchain {
    languageVersion = project.property("javaCurrentLang") as JavaLanguageVersion
  }
}

jmh {
  resultFormat = "json"
}

tasks.named("compileJmhJava") {
  dependsOn(":fs-core:compileJava17")
}