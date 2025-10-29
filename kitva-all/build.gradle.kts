plugins {
  `java-library`
  jacoco
  `test-report-aggregation`
  id("kitva")
  id("kitva-publish")
}

description = "Aggregation of KitVa modules including annotations, core, and etc."

dependencies {
  testReportAggregation(project(":kitva-annotations"))
  testReportAggregation(project(":kitva-core"))
  testReportAggregation(project(":kitva-internal"))
}

jacoco {
  val jacocoToolVersion: String by project
  toolVersion = jacocoToolVersion
  reportsDirectory = layout.buildDirectory.dir("reports/coverage")
}

tasks.test {
  dependsOn(
    project(":kitva-annotations").tasks.test,
    project(":kitva-core").tasks.test,
    project(":kitva-internal").tasks.test,
  )
  outputs.cacheIf { false }
  outputs.upToDateWhen { false }
  finalizedBy(tasks.jacocoTestReport)
  reports {
    html.required = false
  }
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  executionData(
    project(":kitva-annotations").file("build/jacoco/test.exec"),
    project(":kitva-core").file("build/jacoco/test.exec"),
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
    //html.outputLocation = layout.buildDirectory.dir("reports3/html")
  }
}

tasks.register("cleanReport") {
  group = "verification"
  doLast {
    delete(layout.buildDirectory.dir("reports"))
  }
}

tasks.register("testReport") {
  group = "verification"
  dependsOn(
    tasks.named("cleanReport"),
    tasks.testAggregateTestReport
  )
}