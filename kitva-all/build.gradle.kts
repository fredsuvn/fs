plugins {
  `java-library`
  jacoco
  `test-report-aggregation`
  id("kitva")
  id("kitva-publish")
}

description = "Aggregation of KitVa modules including annotations, core, and etc."

dependencies {
  implementation(platform(project(":kitva-dependencies")))
  implementation(project(":kitva-annotations"))
  implementation(project(":kitva-core"))
  implementation(project(":kitva-internal"))
  testReportAggregation(project(":kitva-annotations"))
  testReportAggregation(project(":kitva-core"))
  testReportAggregation(project(":kitva-internal"))
}

evaluationDependsOn(":kitva-annotations")
evaluationDependsOn(":kitva-core")
evaluationDependsOn(":kitva-internal")

java {
  withJavadocJar()
  withSourcesJar()
  toolchain {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
}

tasks.named<Jar>("jar") {
  from(
    project(":kitva-annotations").sourceSets.main.get().output,
    project(":kitva-core").sourceSets.main.get().output,
  )
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
  //archiveFileName.set("kitva.jar")
}

tasks.named<Jar>("sourcesJar") {
  from(
    project(":kitva-annotations").sourceSets.main.get().allSource,
    project(":kitva-core").sourceSets.main.get().allSource,
  )
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<Javadoc>("javadoc") {

  val projectsToDocument = listOf(
    project(":kitva-annotations"),
    project(":kitva-core"),
  )
  source = files(projectsToDocument.flatMap { project ->
    project.the<JavaPluginExtension>().sourceSets.main.get().allJava.srcDirs
  }).asFileTree
  classpath = files(projectsToDocument.flatMap { project ->
    listOf(
      project.sourceSets.main.get().compileClasspath,
      project.sourceSets.main.get().output
    )
  })

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

tasks.test {
  dependsOn(
    project(":kitva-annotations").tasks.test,
    project(":kitva-core").tasks.test,
    project(":kitva-core").tasks.named("testJava17"),
    project(":kitva-internal").tasks.test,
  )
  reports {
    html.required = false
  }
}

jacoco {
  val jacocoToolVersion: String by project
  toolVersion = jacocoToolVersion
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  executionData(
    project(":kitva-annotations").file("build/jacoco/test.exec"),
    project(":kitva-core").file("build/jacoco/test.exec"),
    project(":kitva-core").file("build/jacoco/testJava17.exec"),
    project(":kitva-internal").file("build/jacoco/test.exec"),
  )
  sourceSets(
    project(":kitva-annotations").sourceSets.main.get(),
    project(":kitva-core").sourceSets.main.get(),
    project(":kitva-internal").sourceSets.main.get(),
  )
  reports {
    xml.required = false
    csv.required = false
    html.required = true
  }
}

tasks.testAggregateTestReport {
  dependsOn(tasks.test)
  //testResults.
  testResults.from(
    // kitva-annotations的默认test任务结果
    project(":kitva-annotations").layout.buildDirectory.dir("test-results/test"),
    // kitva-core的默认test任务结果
    project(":kitva-core").layout.buildDirectory.dir("test-results/test"),
    // kitva-core的testJava17任务结果（精确到子目录）
    project(":kitva-core").layout.buildDirectory.dir("test-results/testJava17"),
    // kitva-internal的默认test任务结果
    project(":kitva-internal").layout.buildDirectory.dir("test-results/test")
  )
  doFirst {
    println("聚合的测试结果目录：")
    testResults.files.forEach { file ->
      println("- ${file.absolutePath}")
    }
  }
}