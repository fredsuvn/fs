plugins {
  `java-library`
  jacoco
  id("kitva")
  id("kitva-publish")
}

description = "Collection of annotations supporting static analyses and providing partial of jsr305."

dependencies {
  //implementation platform(project(":kitva-dependencies"))
  //implementation("com.google.code.findbugs:jsr305")
  testImplementation(platform(project(":kitva-dependencies")))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
  withJavadocJar()
  withSourcesJar()
  toolchain {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
  reports {
    html.required = false
  }
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
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
    languageVersion = project.property("javaCurrentLang") as JavaLanguageVersion
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