plugins {
  id("kitva")
  id("java-library")
  id("kitva-publish")
}

description = "Collection of annotations supporting static analyses and partial jsr305."

dependencies {
  //implementation platform(project(":kitva-dependencies"))
  //implementation("com.google.code.findbugs:jsr305")

  testImplementation(platform(project(":kitva-dependencies")))
  //testImplementation("org.testng:testng:7.5.1")
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly ("org.junit.jupiter:junit-jupiter-engine")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
  toolchain {
    val toJavaVersion: JavaLanguageVersion by project
    languageVersion.set(toJavaVersion)
  }
  withJavadocJar()
  withSourcesJar()
}

tasks.withType<Javadoc>().configureEach {
  destinationDir = file("$projectDir/docs/javadoc")
  (options as StandardJavadocDocletOptions).apply {
    encoding = "UTF-8"
    locale = "en_US"
  }
}

tasks.clean {
  //delete(tasks.javadoc.get().destinationDir)
}

tasks.register("cleanWithJavadoc") {
  dependsOn(tasks.clean)
  group = "build"
  doLast {
    delete(tasks.javadoc.get().destinationDir)
  }
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
//  useTestNG {
//    suites("src/test/resources/testng.xml")
//  }
}