plugins {
  `java-library`
  id("fs")
}

description = "Eggs of fs."

dependencies {
  annotationProcessor(platform(project(":fs-dependencies")))
  annotationProcessor("org.projectlombok:lombok")

  implementation(platform(project(":fs-dependencies")))
  implementation(project(":fs-annotation"))
  implementation(project(":fs-core"))

  testImplementation(platform(project(":fs-dependencies")))
  testImplementation(project(":fs-internal"))
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
  failOnNoDiscoveredTests = false
  reports {
    html.required = false
  }
}