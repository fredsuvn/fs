plugins {
  `java-library`
  jacoco
  `test-report-aggregation`
  id("fs")
  id("fs-publish")
}

val publishType by extra { "jar" }

description = "Aggregation of fs, including fs-jsr305, fs-annotation and fs-core, without dependencies."

val jsr305Project: Project = project(":fs-jsr305")
val annotationProject: Project = project(":fs-annotation")
val asmProject: Project = project(":fs-asm")
val coreProject: Project = project(":fs-core")
val internalProject: Project = project(":fs-internal")

evaluationDependsOn(jsr305Project.path)
evaluationDependsOn(annotationProject.path)
evaluationDependsOn(asmProject.path)
evaluationDependsOn(coreProject.path)
evaluationDependsOn(internalProject.path)

java {
  withJavadocJar()
  withSourcesJar()
}

tasks.named<Jar>("jar") {
  from(
    jsr305Project.sourceSets.main.get().output,
    annotationProject.sourceSets.main.get().output,
    asmProject.sourceSets.main.get().output,
    coreProject.sourceSets.main.get().output,
  )
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
  //archiveFileName.set("fs.jar")
}

tasks.named<Jar>("sourcesJar") {
  from(
    jsr305Project.sourceSets.main.get().allSource,
    annotationProject.sourceSets.main.get().allSource,
    asmProject.sourceSets.main.get().allSource,
    coreProject.sourceSets.main.get().allSource,
  )
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.named<Javadoc>("javadoc") {

  val projectsToDocument = listOf(
    jsr305Project,
    annotationProject,
    asmProject,
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
    languageVersion = project.property("javaLangVersionTo") as JavaLanguageVersion
  }

  //destinationDir = rootDir.resolve("docs/javadoc")
}

val testByJava8: TaskProvider<Task> = coreProject.tasks.named("testByJava8")
val testByJava17: TaskProvider<Task> = coreProject.tasks.named("testByJava17")

tasks.test {
  dependsOn(
    jsr305Project.tasks.test,
    annotationProject.tasks.test,
    asmProject.tasks.test,
    //coreProject.tasks.test,
    testByJava8,
    testByJava17,
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
    jsr305Project.file("build/jacoco/test.exec"),
    annotationProject.file("build/jacoco/test.exec"),
    //asmProject.file("build/jacoco/test.exec"),
    //coreProject.file("build/jacoco/test.exec"),
    coreProject.file("build/jacoco/${testByJava8.name}.exec"),
    coreProject.file("build/jacoco/${testByJava17.name}.exec"),
    internalProject.file("build/jacoco/test.exec"),
  )
  sourceSets(
    jsr305Project.sourceSets.main.get(),
    annotationProject.sourceSets.main.get(),
    //asmProject.sourceSets.main.get(),
    coreProject.sourceSets.main.get(),
    internalProject.sourceSets.main.get(),
  )
  reports {
    xml.required = false
    csv.required = false
    html.required = true
    //html.outputLocation = rootDir.resolve("docs/reports/jacoco")
  }
}

tasks.testAggregateTestReport {
  dependsOn(tasks.test)
  testResults.from(
    jsr305Project.layout.buildDirectory.dir("test-results/test"),
    jsr305Project.layout.buildDirectory.dir("test-results/test/binary"),
    annotationProject.layout.buildDirectory.dir("test-results/test"),
    annotationProject.layout.buildDirectory.dir("test-results/test/binary"),
    //asmProject.layout.buildDirectory.dir("test-results/test"),
    //asmProject.layout.buildDirectory.dir("test-results/test/binary"),
    //coreProject.layout.buildDirectory.dir("test-results/test"),
    //coreProject.layout.buildDirectory.dir("test-results/test/binary"),
    coreProject.layout.buildDirectory.dir("test-results/${testByJava8.name}"),
    coreProject.layout.buildDirectory.dir("test-results/${testByJava8.name}/binary"),
    coreProject.layout.buildDirectory.dir("test-results/${testByJava17.name}"),
    coreProject.layout.buildDirectory.dir("test-results/${testByJava17.name}/binary"),
    internalProject.layout.buildDirectory.dir("test-results/test"),
    internalProject.layout.buildDirectory.dir("test-results/test/binary"),
  )
  //destinationDirectory = rootDir.resolve("docs/reports/test-aggregate")
}