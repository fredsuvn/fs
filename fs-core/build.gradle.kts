plugins {
  `java-library`
  jacoco
  id("com.google.protobuf")
  id("fs")
  id("fs-publish")
}

val publishType by extra { "jar" }

description = "Core of fs, including core kits and interfaces with their default implementations."

val projectVersion: String by project
val toJavaVersion: JavaLanguageVersion by project

dependencies {

  annotationProcessor(platform(project(":fs-dependencies")))
  annotationProcessor("org.projectlombok:lombok")
  annotationProcessor(project(":fs-build"))

  compileOnly(platform(project(":fs-dependencies")))
  compileOnly(project(":fs-build"))

  //compileOnly("org.projectlombok:lombok")
  //compileOnly("org.springframework:spring-core")
  //compileOnly("cglib:cglib")
  compileOnly("com.google.protobuf:protobuf-java")

  api(project(":fs-annotation"))
  api(project(":fs-asm"))

  testAnnotationProcessor(platform(project(":fs-dependencies")))
  testAnnotationProcessor("org.projectlombok:lombok")

  testCompileOnly(platform(project(":fs-dependencies")))
  testCompileOnly("org.projectlombok:lombok")

  testImplementation(platform(project(":fs-dependencies")))
  testImplementation(project(":fs-internal"))
  testImplementation("com.google.protobuf:protobuf-java")
  testImplementation("org.bouncycastle:bcpkix-jdk18on")
  //testImplementation("org.mockito:mockito-core")
  testImplementation("jakarta.annotation:jakarta.annotation-api")

  // jetty
  testImplementation(platform("org.eclipse.jetty:jetty-bom"))
  testImplementation("org.eclipse.jetty:jetty-server")
  testImplementation("org.eclipse.jetty:jetty-servlet")
  testImplementation("javax.servlet:javax.servlet-api")
}

java {
  withJavadocJar()
  withSourcesJar()
}

sourceSets {
  main {
    java {
      srcDirs("src/main/java")
    }
  }
  test {
    proto {
      //srcDirs("src/test/proto")
    }
    java {
      srcDirs("src/test/java")
    }
  }
}

val implByJvm = "ImplByJ"
val javaVersionFrom = project.property("javaLangVersionFrom") as JavaLanguageVersion
val javaVersionTo = project.property("javaLangVersionTo") as JavaLanguageVersion
val javaVerFrom = javaVersionFrom.asInt()
val javaVerTo = javaVersionTo.asInt()

// java8 base
tasks.register("compileJava${javaVerFrom}", JavaCompile::class) {
  group = "compile"
  val fsVersion = tasks.named("fsVersion")
  dependsOn(fsVersion)
  source = sourceSets.main.get().allJava
  classpath = sourceSets.main.get().compileClasspath
  exclude("**/*${implByJvm}*.java")
  destinationDirectory = layout.buildDirectory.dir("classes/java/main").get().asFile
  javaCompiler = javaToolchains.compilerFor {
    languageVersion = javaVersionFrom
  }
  options.annotationProcessorPath = configurations.getByName("annotationProcessor")
}

// 9-17
((javaVerFrom + 1)..javaVerTo).forEach { javaVersion ->
  val taskName = "compileJava$javaVersion"
  tasks.register(taskName, JavaCompile::class) {
    group = "compile"
    val lastCompileTask = tasks.named<JavaCompile>("compileJava${javaVersion - 1}")
    dependsOn(lastCompileTask)
    source = sourceSets.main.get().allJava
    classpath = sourceSets.main.get().compileClasspath + files(lastCompileTask.get().destinationDirectory)
    include("**/*${implByJvm + javaVersion}.java")
    destinationDirectory = layout.buildDirectory.dir("classes/java/main").get().asFile
    javaCompiler = javaToolchains.compilerFor {
      languageVersion = javaVersionTo
    }
    options.compilerArgs.add("--release")
    options.compilerArgs.add(javaVersion.toString())
    options.annotationProcessorPath = configurations.getByName("annotationProcessor")
  }
}

val compileJavaHighest = tasks.named<JavaCompile>("compileJava$javaVerTo")

tasks.compileJava {
  group = "compile"
  enabled = false
  dependsOn(compileJavaHighest)
}

tasks.named("classes") {
  dependsOn(compileJavaHighest)
}

// java8 base
tasks.register("compileTestJava$javaVerFrom", JavaCompile::class) {
  group = "compile"
  dependsOn(compileJavaHighest)
  source = sourceSets.test.get().allJava
  classpath = sourceSets.test.get().compileClasspath
  exclude("**/*${implByJvm}*Test.java")
  destinationDirectory = file(layout.buildDirectory.dir("classes/java/test"))
  javaCompiler = javaToolchains.compilerFor {
    languageVersion = javaVersionFrom
  }
  options.annotationProcessorPath = configurations.getByName("testAnnotationProcessor")
}

// 9-17
((javaVerFrom + 1)..javaVerTo).forEach { javaVersion ->
  val taskName = "compileTestJava$javaVersion"
  tasks.register(taskName, JavaCompile::class) {
    group = "compile"
    dependsOn(tasks.named("compileTestJava${javaVersion - 1}"))
    source = sourceSets.test.get().allJava
    classpath = sourceSets.test.get().compileClasspath
    include("**/*${implByJvm + javaVersion}Test.java")
    destinationDirectory = file(layout.buildDirectory.dir("classes/java/test"))
    javaCompiler = javaToolchains.compilerFor {
      languageVersion = javaVersionTo
    }
    options.compilerArgs.add("--release")
    options.compilerArgs.add(javaVersion.toString())
    options.annotationProcessorPath = configurations.getByName("testAnnotationProcessor")
  }
}

val compileTestJavaHighest = tasks.named<JavaCompile>("compileTestJava$javaVerTo")

tasks.compileTestJava {
  group = "compile"
  enabled = false
  dependsOn(tasks.named("generateTestProto"), compileTestJavaHighest)
}

tasks.named<Jar>("sourcesJar") {
  from(sourceSets.main.get().allJava)
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  exclude("**/*${implByJvm}*Test.class")
  useJUnitPlatform {
    excludeTags("J17Only")
  }
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = javaVersionFrom
  }
  failOnNoDiscoveredTests = false
  reports {
    html.required = false
  }
}

val testJavaHighest by tasks.registering(Test::class) {
  dependsOn(compileTestJavaHighest)
  group = "verification"
  testClassesDirs = fileTree(layout.buildDirectory.dir("classes/java/test"))
  classpath = sourceSets.test.get().runtimeClasspath
  ((javaVerFrom + 1)..javaVerTo).forEach { jv ->
    classpath += files(tasks.named<JavaCompile>("compileJava$jv").get().destinationDirectory)
    classpath += files(tasks.named<JavaCompile>("compileTestJava$jv").get().destinationDirectory)
  }
  useJUnitPlatform {
    includeTags("J17Also", "J17Only")
  }
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = javaVersionTo
  }
  failOnNoDiscoveredTests = false
  reports {
    html.required = false
  }
}

tasks.check.get().dependsOn(tasks.test, testJavaHighest)

tasks.named<Javadoc>("javadoc") {
  val ops = options as StandardJavadocDocletOptions
  ops.encoding = "UTF-8"
  ops.locale = "en-us"
  ops.charSet = "UTF-8"
  ops.docEncoding = "UTF-8"
  ops.jFlags("-Duser.language=en", "-Duser.country=US")
  ops.addStringOption("Xdoclint:none", "-quiet")
  javadocTool = javaToolchains.javadocToolFor {
    languageVersion = javaVersionTo
  }
}

jacoco {
  val jacocoToolVersion: String by project
  toolVersion = jacocoToolVersion
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    html.required = true
    xml.required = false
    csv.required = false
  }
}

protobuf {
  //generatedFilesBaseDir = protoPath
  protoc {
    val protocToolVersion: String by project
    artifact = "com.google.protobuf:protoc:${protocToolVersion}"
    // generatedFilesBaseDir = protoPath
  }
  plugins {
    //grpc { artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion" }
    //grpckt { artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion" }
  }
  generateProtoTasks {
    ofSourceSet("test").forEach { task ->
      task.plugins {
        //create("java") { outputSubDir = "proto222" }
        //grpc { outputDir = file("$buildDir/proto22/test/java") }
      }
    }
  }
}

tasks.register("cleanProto") {
  doLast {
    deleteProtoGeneratedFiles()
  }
}

tasks.clean {
  doLast {
    deleteProtoGeneratedFiles()
  }
}

fun deleteProtoGeneratedFiles() {
  delete(protobuf.generatedFilesBaseDir)
}

tasks.register("fsVersion") {
  group = "version"
  description = "Set FS.LIB_VERSION"
  doLast {
    val fsVersion = rootProject.version.toString()
    val originCodes = file("src/main/java/space/sunqian/fs/Fs.java")
      .readText()
    val fieldRegex = "LIB_VERSION = \"([^\"]*)\"".toRegex()
    val commentRegex = "\\* \\<pre\\>\\{@code ([^ ]*) \\}\\</pre\\>".toRegex()
    val newCodes = fieldRegex.replace(originCodes, "LIB_VERSION = \"$fsVersion\"")
      .let { commentRegex.replace(it, "* <pre>{@code $fsVersion }</pre>") }
    //println("Replace: $originCodes -> $newCodes")
    if (newCodes != originCodes) {
      file("src/main/java/space/sunqian/fs/Fs.java").writeText(newCodes)
    }
  }
}