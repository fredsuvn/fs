plugins {
  `java-library`
  jacoco
  `maven-publish`
  signing
  id("com.google.protobuf")
  id("kitva")
}

description = "Core of KitVa, including core kits and interfaces with their default implementations."

val projectVersion: String by project
val toJavaVersion: JavaLanguageVersion by project

dependencies {

  annotationProcessor(platform(project(":kitva-dependencies")))
  annotationProcessor("org.projectlombok:lombok")

  implementation(platform(project(":kitva-dependencies")))

  compileOnly("org.projectlombok:lombok")
  compileOnly("org.springframework:spring-core")
  compileOnly("cglib:cglib")
  compileOnly("com.google.protobuf:protobuf-java")

  api(project(":kitva-annotations"))

  testAnnotationProcessor(platform(project(":kitva-dependencies")))
  testAnnotationProcessor("org.projectlombok:lombok")

  testImplementation(platform(project(":kitva-dependencies")))
  testImplementation(project(":kitva-internal"))
  testImplementation("com.google.protobuf:protobuf-java")
  testImplementation("org.bouncycastle:bcpkix-jdk18on")
  testImplementation("org.mockito:mockito-core")
  testImplementation("jakarta.annotation:jakarta.annotation-api")
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
val maxJvmVersion = 17

tasks.compileJava {
  group = "compile"
  enabled = false
}

val compileJava8 by tasks.registering(JavaCompile::class) {
  group = "compile"
  source = sourceSets.main.get().allJava
  classpath = sourceSets.main.get().compileClasspath
  exclude("**/*${implByJvm}*.java")
  destinationDirectory = file(layout.buildDirectory.dir("/classes/java/main"))
  javaCompiler = javaToolchains.compilerFor {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
  options.annotationProcessorPath = configurations.getByName("annotationProcessor")
}

(9..maxJvmVersion).forEach { jvmVersion ->
  val taskName = "compileJava$jvmVersion"
  tasks.register(taskName, JavaCompile::class) {
    group = "compile"
    dependsOn(tasks.compileJava)
    source = sourceSets.main.get().allJava
    classpath = sourceSets.main.get().compileClasspath + files(tasks.compileJava.get().destinationDirectory)
    (8..<jvmVersion).forEach { jv ->
      dependsOn += tasks.named("compileJava$jv")
      classpath += files(tasks.named<JavaCompile>("compileJava$jv").get().destinationDirectory)
    }
    include("**/*${implByJvm + jvmVersion}.java")
    destinationDirectory = file(layout.buildDirectory.dir("/classes/java/main"))
    javaCompiler = javaToolchains.compilerFor {
      languageVersion = project.property("javaCurrentLang") as JavaLanguageVersion
    }
    options.compilerArgs.add("--release")
    options.compilerArgs.add(jvmVersion.toString())
  }
}

val compileJavaMax = tasks.named<JavaCompile>("compileJava$maxJvmVersion")

tasks.named("classes") {
  dependsOn(compileJavaMax)
}

tasks.compileTestJava {
  group = "compile"
  enabled = false
}

val compileTestJava8 by tasks.registering(JavaCompile::class) {
  group = "compile"
  dependsOn(compileJavaMax)
  source = sourceSets.test.get().allJava
  classpath = sourceSets.test.get().compileClasspath
  exclude("**/*${implByJvm}*Test.java")
  destinationDirectory = file(layout.buildDirectory.dir("/classes/java/test"))
  javaCompiler = javaToolchains.compilerFor {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
  options.annotationProcessorPath = configurations.getByName("testAnnotationProcessor")
}

(9..maxJvmVersion).forEach { jvmVersion ->
  val taskName = "compileTestJava$jvmVersion"
  tasks.register(taskName, JavaCompile::class) {
    group = "compile"
    //dependsOn(compileTestJava8)
    source = sourceSets.test.get().allJava
    classpath = sourceSets.test.get().compileClasspath
    (8..<jvmVersion).forEach { jv ->
      dependsOn += tasks.named("compileTestJava$jv")
      classpath += files(tasks.named<JavaCompile>("compileTestJava$jv").get().destinationDirectory)
    }
    include("**/*${implByJvm + jvmVersion}Test.java")
    destinationDirectory = file(layout.buildDirectory.dir("/classes/java/test"))
    javaCompiler = javaToolchains.compilerFor {
      languageVersion = project.property("javaCurrentLang") as JavaLanguageVersion
    }
    options.compilerArgs.add("--release")
    options.compilerArgs.add(jvmVersion.toString())
    options.annotationProcessorPath = configurations.getByName("testAnnotationProcessor")
  }
}

val compileTestJavaMax = tasks.named<JavaCompile>("compileTestJava$maxJvmVersion")

tasks.named("compileTestJava") {
  dependsOn(tasks.named("generateTestProto"), compileTestJavaMax)
}

tasks.named<Jar>("sourcesJar") {
  from(sourceSets.main.get().allJava)
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  exclude("**/*${implByJvm}*Test.class")
  useJUnitPlatform() {
    excludeTags("J17Only")
  }
  failOnNoDiscoveredTests = false
  reports {
    html.required = false
  }
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
}

val testJava17 by tasks.registering(Test::class) {
  dependsOn(compileTestJavaMax)
  group = "verification"
  testClassesDirs = fileTree(layout.buildDirectory.dir("/classes/java/test"))
  classpath = sourceSets.test.get().runtimeClasspath
  (9..maxJvmVersion).forEach { jv ->
    classpath += files(tasks.named<JavaCompile>("compileJava$jv").get().destinationDirectory)
    classpath += files(tasks.named<JavaCompile>("compileTestJava$jv").get().destinationDirectory)
  }
  //include("**/*${j17Suffix}Test.class")
  //include("**/*MultiJvmTest.class")
  useJUnitPlatform() {
    includeTags("J17Also", "J17Only")
  }
  failOnNoDiscoveredTests = false
  reports {
    html.required = false
  }
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = project.property("javaCurrentLang") as JavaLanguageVersion
  }
}
tasks.check.get().dependsOn(tasks.test, testJava17)

jacoco {
  val jacocoToolVersion: String by project
  toolVersion = jacocoToolVersion
}

tasks.named<Javadoc>("javadoc") {
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