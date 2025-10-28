plugins {
  id("kitva")
  id("kitva-publish")
  `java-library`
  jacoco
}

description = "Collection of annotations supporting static analyses and partial jsr305."

dependencies {
  //implementation platform(project(":kitva-dependencies"))
  //implementation("com.google.code.findbugs:jsr305")
  testImplementation(platform(project(":kitva-dependencies")))
  testImplementation("org.junit.jupiter:junit-jupiter-api")
  testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
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
  outputs.cacheIf { false }
  outputs.upToDateWhen { false }
  finalizedBy(tasks.jacocoTestReport)

  reports {
    html.outputLocation.set(layout.buildDirectory.dir("reports2/custom-test-html"))
    //junitXml.outputLocation.set(layout.buildDirectory.dir("reports2/custom-test-xml"))
  }
}
tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    xml.required = false
    csv.required = false
    html.outputLocation = layout.buildDirectory.dir("reports3/html")
  }
}
jacoco {
  val jacocoToolVersion: String by project
  toolVersion = jacocoToolVersion
  //reportsDirectory = layout.buildDirectory.dir("customJacocoReportDir")
}