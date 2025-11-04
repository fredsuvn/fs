plugins {
  `java-library`
  jacoco
  `test-report-aggregation`
  id("kitva")
  id("kitva-publish")
}

description = "Aggregation of KitVa modules including annotations, core, and etc."

val annotationProject = project(":kitva-annotations")
val coreProject = project(":kitva-core")
val internalProject = project(":kitva-internal")

dependencies {
  implementation(platform(project(":kitva-dependencies")))
  implementation(annotationProject)
  implementation(coreProject)
  implementation(internalProject)
  testReportAggregation(annotationProject)
  testReportAggregation(coreProject)
  testReportAggregation(internalProject)
}

evaluationDependsOn(annotationProject.path)
evaluationDependsOn(coreProject.path)
evaluationDependsOn(internalProject.path)

java {
  withJavadocJar()
  withSourcesJar()
  toolchain {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
}

tasks.named<Jar>("jar") {
  from(
    annotationProject.sourceSets.main.get().output,
    coreProject.sourceSets.main.get().output,
  )
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
  //archiveFileName.set("kitva.jar")
}

tasks.named<Jar>("sourcesJar") {
  from(
    annotationProject.sourceSets.main.get().allSource,
    coreProject.sourceSets.main.get().allSource,
  )
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<Javadoc>("javadoc") {

  val projectsToDocument = listOf(
    annotationProject,
    coreProject,
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

val testByJ17 = coreProject.tasks.named("testByJ17")

tasks.test {
  dependsOn(
    annotationProject.tasks.test,
    coreProject.tasks.test,
    testByJ17,
    internalProject.tasks.test,
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
    annotationProject.file("build/jacoco/test.exec"),
    coreProject.file("build/jacoco/test.exec"),
    coreProject.file("build/jacoco/${testByJ17.name}.exec"),
    internalProject.file("build/jacoco/test.exec"),
  )
  sourceSets(
    annotationProject.sourceSets.main.get(),
    coreProject.sourceSets.main.get(),
    internalProject.sourceSets.main.get(),
  )
  reports {
    xml.required = false
    csv.required = false
    html.required = true
  }
}

tasks.testAggregateTestReport {
  dependsOn(tasks.test)
  testResults.from(
    coreProject.layout.buildDirectory.dir("test-results/${testByJ17.name}"),
    coreProject.layout.buildDirectory.dir("test-results/${testByJ17.name}/binary"),
  )
}