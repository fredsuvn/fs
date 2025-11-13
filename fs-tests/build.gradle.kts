plugins {
  `java-library`
  id("me.champeau.jmh")
  id("fs")
}

description = "Tests for fs."

dependencies {
  implementation(platform(project(":fs-dependencies")))
  implementation(project(":fs-annotations"))
  implementation(project(":fs-core"))
  implementation(project(":fs-internal"))

  jmh(platform(project(":fs-dependencies")))
  jmh(project(":fs-annotations"))
  jmh(project(":fs-core"))
  jmh(project(":fs-internal"))
  jmh("org.openjdk.jmh:jmh-generator-annprocess")

  testImplementation(platform(project(":fs-dependencies")))
  testImplementation(project(":fs-internal"))
}

java {
  toolchain {
    languageVersion = project.property("javaCurrentLang") as JavaLanguageVersion
  }
}

sourceSets {
  val jmh = getByName("jmh")
  main {
    java {
      srcDirs += jmh.java
    }
  }
}

jmh {
  resultFormat = "json"
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
  failOnNoDiscoveredTests = false
  reports {
    html.required = false
  }
}