plugins {
  `java-library`
  jacoco
  id("fs")
  id("fs-publish")
}

description = "The built-in ASM framework for fs."
val publishType by extra { "jar" }

dependencies {
  testImplementation(platform(project(":fs-dependencies")))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

val javaVersionFrom = project.property("javaLangVersionFrom") as JavaLanguageVersion
val javaVersionTo = project.property("javaLangVersionTo") as JavaLanguageVersion

java {
  withJavadocJar()
  withSourcesJar()
  toolchain {
    languageVersion = javaVersionFrom
  }
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
  reports {
    html.required = false
  }
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = javaVersionFrom
  }
}

tasks.named<Javadoc>("javadoc") {
  val ops = options as StandardJavadocDocletOptions
  ops.encoding = "UTF-8"
  ops.locale = "en-us"
  ops.charSet = "UTF-8"
  ops.docEncoding = "UTF-8"
  ops.jFlags("-Duser.language=en", "-Duser.country=US")
  ops.addStringOption("Xdoclint:none", "-quiet")
  javadocTool = javaToolchains.javadocToolFor {
    languageVersion = javaVersionTo
  }
}

jacoco {
  val jacocoToolVersion: String by project
  toolVersion = jacocoToolVersion
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    html.required = false
    xml.required = false
    csv.required = false
  }
}