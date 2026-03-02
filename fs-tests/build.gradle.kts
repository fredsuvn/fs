plugins {
  `java-library`
  id("me.champeau.jmh")
  id("fs")
}

description = "Tests and benchmarks for fs."

dependencies {
  annotationProcessor(platform(project(":fs-dependencies")))
  annotationProcessor("org.projectlombok:lombok")

  implementation("org.springframework.boot:spring-boot-starter")

  implementation(platform(project(":fs-dependencies")))
  implementation(project(":fs-annotation"))
  implementation(project(":fs-core"))
  implementation(project(":fs-internal"))

  //implementation("cglib:cglib")

  jmh(platform(project(":fs-dependencies")))
  jmh(project(":fs-annotation"))
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
  /*
  includes = listOf(
    "internal.tests.benchmarks.CopyPropertiesBenchmark"
    //"internal.tests.benchmarks.(AspectBenchmark)"
    //"internal.tests.benchmarks.TcpServerBenchmark"
  )
   */
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
  failOnNoDiscoveredTests = false
  reports {
    html.required = false
  }
}