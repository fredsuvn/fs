plugins {
  `java-library`
  jacoco
  `test-report-aggregation`
  `maven-publish`
  signing
  id("fs")
}

description = "Aggregation of fs, including fs-annotations and fs-core, without dependencies."

val annotationProject = project(":fs-annotations")
val coreProject = project(":fs-core")
val internalProject = project(":fs-internal")

evaluationDependsOn(annotationProject.path)
evaluationDependsOn(coreProject.path)
evaluationDependsOn(internalProject.path)

java {
  withJavadocJar()
  withSourcesJar()
  //  toolchain {
  //    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  //  }
}

tasks.named<Jar>("jar") {
  from(
    annotationProject.sourceSets.main.get().output,
    coreProject.sourceSets.main.get().output,
  )
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
  //archiveFileName.set("fs.jar")
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

  destinationDir = rootDir.resolve("docs/javadoc")
}

val testJava17 = coreProject.tasks.named("testJava17")

tasks.test {
  dependsOn(
    annotationProject.tasks.test,
    coreProject.tasks.test,
    testJava17,
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
    coreProject.file("build/jacoco/${testJava17.name}.exec"),
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
    //html.outputLocation = rootDir.resolve("docs/reports/jacoco")
  }
}

tasks.testAggregateTestReport {
  dependsOn(tasks.test)
  testResults.from(
    annotationProject.layout.buildDirectory.dir("test-results/test"),
    annotationProject.layout.buildDirectory.dir("test-results/test/binary"),
    coreProject.layout.buildDirectory.dir("test-results/test"),
    coreProject.layout.buildDirectory.dir("test-results/test/binary"),
    coreProject.layout.buildDirectory.dir("test-results/${testJava17.name}"),
    coreProject.layout.buildDirectory.dir("test-results/${testJava17.name}/binary"),
    internalProject.layout.buildDirectory.dir("test-results/test"),
    internalProject.layout.buildDirectory.dir("test-results/test/binary"),
  )
  //destinationDirectory = rootDir.resolve("docs/reports/test-aggregate")
}

publishing {
  publications {
    create<MavenPublication>("main") {
      from(components["java"])
      val projectInfo: ProjectInfo by rootProject.extra
      pom {
        version = projectInfo.version
        group = rootProject.group
        name = project.name
        description = project.description
        url = projectInfo.url
        licenses {
          projectInfo.licenses.forEach {
            license {
              name.set(it.name)
              url.set(it.url)
            }
          }
        }
        developers {
          projectInfo.developers.forEach {
            developer {
              id.set(it.id)
              name.set(it.name)
              email.set(it.email)
              url.set(it.url)
            }
          }
        }
        scm {
          connection = projectInfo.scm.connection
          developerConnection = projectInfo.scm.developerConnection
          url = projectInfo.scm.url
        }
      }
    }
  }
  repositories {
    mavenLocal()
  }
}

signing {
}