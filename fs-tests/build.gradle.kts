plugins {
  `java-library`
  id("me.champeau.jmh")
  id("fs")
}

description = "Tests and benchmarks for fs."

dependencies {
  annotationProcessor(platform(project(":fs-dependencies")))
  annotationProcessor("org.projectlombok:lombok")

  implementation(platform(project(":fs-dependencies")))
  implementation(project(":fs-annotations"))
  implementation(project(":fs-core"))
  implementation(project(":fs-internal"))

  implementation("cglib:cglib")

  jmh(platform(project(":fs-dependencies")))
  jmh(project(":fs-annotations"))
  jmh(project(":fs-core"))
  jmh(project(":fs-internal"))
  jmh("org.openjdk.jmh:jmh-generator-annprocess")

  //testImplementation(platform(project(":fs-dependencies")))
  //testImplementation(project(":fs-internal"))
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
  includes = listOf(
    //"internal.tests.benchmarks.(AspectBenchmark|CopyPropertiesBenchmark)"
    "internal.tests.benchmarks.TcpServerBenchmark"
  )
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
  failOnNoDiscoveredTests = false
  reports {
    html.required = false
  }
}