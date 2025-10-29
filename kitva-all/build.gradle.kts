plugins {
  `java-library`
  jacoco
  `test-report-aggregation`
  id("kitva")
  id("kitva-publish")
}

description = "Aggregation of KitVa modules including annotations, core, and etc."

dependencies {
  //testReportAggregation(project(":kitva-annotations"))
  testReportAggregation(project(":kitva-core"))
  //testReportAggregation(project(":kitva-internal"))
}

tasks.jar {
  from(
    project(":kitva-annotations").sourceSets.main.get().output,
    project(":kitva-core").sourceSets.main.get().output,
  )
}

tasks.javadoc {

  val projectsToDocument = listOf(
    project(":kitva-annotations"),
    project(":kitva-core"),
    project(":kitva-internal"),
  )
  // 收集源文件
  source = files(projectsToDocument.flatMap { project ->
    project.sourceSets.main.get().allJava.srcDirs
  }).asFileTree

  // 设置类路径
  classpath = files(projectsToDocument.flatMap { project ->
    listOf(
      project.sourceSets.main.get().compileClasspath,
      project.sourceSets.main.get().output
    )
  })
  options.encoding = "UTF-8"
  options.locale = "en_US"
  val opt = options as StandardJavadocDocletOptions
  opt.charSet = "UTF-8"
  opt.memberLevel = JavadocMemberLevel.PUBLIC
  //opt.

  // 输出目录
  //setDestinationDir(file("${buildDir}/docs/javadoc"))

  // 配置选项
//  (options as StandardJavadocDocletOptions).apply {
//    encoding = "UTF-8"
//    charSet = "UTF-8"
//    memberLevel = JavadocMemberLevel.PUBLIC

    // 外部 API 文档链接
    //links("https://docs.oracle.com/javase/8/docs/api/")
    //links("https://javadoc.io/doc/org.springframework/spring-core/5.3.0/")

    // 控制台输出更安静
    //addBooleanOption("quiet", true)

    // 忽略缺少的注释
    //addBooleanOption("Xdoclint:none", true)

    // 启用 HTML5 输出
    // addBooleanOption("html5", true)

    // 设置窗口标题
    //docTitle = "${project.name} API Documentation"
    //windowTitle = "${project.name} API"

    // 底部版权信息
    //bottom = "Copyright © 2024 ${project.name}. All rights reserved."
  //}

  // 源文件编码
  //encoding = "UTF-8"

//  options {
//    // 强制转换为StandardJavadocDocletOptions以设置更多参数
//    this as StandardJavadocDocletOptions
//    //charset = "UTF-8"
//    docEncoding = "UTF-8"
//    encoding = "UTF-8"
//    charSet = "UTF-8"
//    //addStringOption("Xdoclint", "all,-private")
//  }

  // 任务依赖
  dependsOn(projectsToDocument.map { it.tasks.named("classes") })
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