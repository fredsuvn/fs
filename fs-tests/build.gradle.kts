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
  implementation("com.alibaba.fastjson2:fastjson2")

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
  test {
    compileClasspath += jmh.output
    runtimeClasspath += jmh.output
  }
  create("samples") {
    java.srcDir("src/samples/java")
    resources.srcDir("src/samples/resources")
  }
}

val samplesImplementation by configurations.getting {
  extendsFrom(configurations.implementation.get())
}

dependencies {
  // samples
  samplesImplementation(platform(project(":fs-dependencies")))
  samplesImplementation(project(":fs-annotation"))
  samplesImplementation(project(":fs-core"))
  samplesImplementation(project(":fs-internal"))
}

jmh {
  resultFormat = "json"
  includeTests = false

  includes = listOf(
    //"internal.benchmark.(CopyPropertiesJmh|CopyPropertiesWithAnnotationsJmh)"
    //"internal.benchmark.AspectJmh"
    //"internal.benchmark.TcpServerJmh"
    "internal.benchmark.JsonParseJmh"
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