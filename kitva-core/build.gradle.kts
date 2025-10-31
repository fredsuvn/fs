plugins {
  `java-library`
  jacoco
  id("kitva")
  id("java-library")
  id("kitva-publish")
  id("com.google.protobuf") version (Versions.protobuf)
}

description = "Core kits and interfaces of KitVa including default implementations."

val projectVersion: String by project
val toJavaVersion: JavaLanguageVersion by project

java {
  registerFeature("proxySpringSupport") {
    usingSourceSet(sourceSets.main.get())
    capability("kitva.build", "proxy-spring-support", projectVersion)
  }
  registerFeature("proxyCglibSupport") {
    usingSourceSet(sourceSets.main.get())
    capability("kitva.build", "proxy-cglib-support", projectVersion)
  }
  registerFeature("dataProtobufSupport") {
    usingSourceSet(sourceSets.main.get())
    capability("kitva.build", "data-protobuf-support", projectVersion)
  }
}

dependencies {

  annotationProcessor(platform(project(":kitva-dependencies")))
  annotationProcessor("org.projectlombok:lombok")

  implementation(platform(project(":kitva-dependencies")))

  //proxySpringSupportImplementation("org.springframework:spring-core")
  //proxyCglibSupportImplementation("cglib:cglib")
  //dataProtobufSupportImplementation("com.google.protobuf:protobuf-java")
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
}

java {
  //toolchain {
  //  languageVersion.set(JavaLanguageVersion.of(8))
  //}
  //withJavadocJar()
  //withSourcesJar()
}

sourceSets {
  main {
    java {
      srcDirs("src/main/java")
      srcDirs("src/main/java8")
    }
  }
  test {
    proto {
      //srcDirs("src/test/proto")
    }
    java {
      srcDirs("src/test/java")
      srcDirs("src/test/java8")
    }
  }
}

tasks.compileJava {
  source = sourceSets.main.get().allJava
  exclude("**/*ImplByJdk17.java")
  options.release.set(8)
}

val compileJava17 by tasks.registering(JavaCompile::class) {
  source = sourceSets.main.get().allJava
  include("**/*ImplByJdk17.java")
  options.release.set(17)
  destinationDirectory.set(file(layout.buildDirectory.dir("/classes/java/main17")))
  classpath = tasks.compileJava.get().classpath + files(tasks.compileJava.get().destinationDirectory)
}

tasks.named("classes") {
  dependsOn(compileJava17)
}

tasks.compileTestJava {
  source = sourceSets.main.get().allJava
  exclude("**/*ImplByJdk17.java")
  options.release.set(8)
}

val compileTestJava17 by tasks.registering(JavaCompile::class) {
  source = sourceSets.main.get().allJava
  include("**/*ImplByJdk17.java")
  options.release.set(17)
  destinationDirectory.set(file(layout.buildDirectory.dir("/classes/java/main17")))
  classpath = tasks.compileJava.get().classpath + files(tasks.compileJava.get().destinationDirectory)
}

tasks.named("compileTestJava") {
  dependsOn(tasks.named("generateTestProto"))
}

tasks.jar {
  from(sourceSets.main.get().output)
  from(fileTree(layout.buildDirectory.dir("/classes/java/main17")))
  manifest.attributes["Multi-Release"] = "true"
}

tasks.javadoc {
  val ops = options as StandardJavadocDocletOptions
  ops.encoding = "UTF-8"
  ops.locale = "en_US"
  ops.charSet = "UTF-8"
  ops.docEncoding = "UTF-8"
  ops.addStringOption("Xdoclint:none", "-quiet")

  doFirst {
    val ops = options as StandardJavadocDocletOptions
    println("Javadoc locale: ${ops.locale}") // 应输出 en_US
  }
}

tasks.register("cleanWithJavadoc") {
  dependsOn(tasks.clean)
  group = "build"
  doLast {
    delete(tasks.javadoc.get().destinationDir)
  }
}

val generatedPath = "$projectDir/generated"
//val protoPath = "$generatedPath/proto"

protobuf {
  //generatedFilesBaseDir = protoPath
  protoc {
    // Download from repositories
    artifact = "com.google.protobuf:protoc:${Versions.protoc}"
    // generatedFilesBaseDir = protoPath
  }

  plugins {
    //grpc { artifact = "io.grpc:protoc-gen-grpc-java:$grpcVersion" }
    //grpckt { artifact = "io.grpc:protoc-gen-grpc-kotlin:$grpcKotlinVersion" }
  }

  generateProtoTasks {
    all().forEach { task ->
      task.plugins {
        //grpc { setOutputSubDir("$protoGenDir") }
        //grpckt {}
      }
    }
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
  //delete("$generatedPath/temp")
}

tasks.test {
  dependsOn(tasks.named("testJava8"), tasks.named("testJava17"))
  //  include("**/*Test.class", "**/*TestKt.class")
  //  exclude("**/java17/*Test.class")
  //  useJUnitPlatform()
  outputs.cacheIf { false }
  outputs.upToDateWhen { false }
  finalizedBy(tasks.jacocoTestReport)
  reports {
    html.required = false
  }
  failOnNoDiscoveredTests = false
}
tasks.jacocoTestReport {
  //dependsOn(tasks.test)
  dependsOn(tasks.named("testJava8"), tasks.named("testJava17"))
  outputs.cacheIf { false }
  outputs.upToDateWhen { false }
  executionData.from(
    fileTree(layout.buildDirectory.dir("jacoco")) {
      include("*.exec")
    }
  )
  // 设置源代码和类文件目录
  sourceDirectories.from(
    sourceSets.main.get().allJava.srcDirs,
    sourceSets.test.get().allJava.srcDirs
  )

  classDirectories.from(
    sourceSets.main.get().output,
    sourceSets.test.get().output
  )
  reports {
    html.required = true
    xml.required = false
    csv.required = false
  }
}
jacoco {
  val jacocoToolVersion: String by project
  toolVersion = jacocoToolVersion
}

tasks.register<Test>("testJava8") {
  group = "Verification"
  description = "Runs Java 8 specific tests"

  testClassesDirs = sourceSets["test"].output.classesDirs
  classpath = sourceSets["test"].runtimeClasspath

  // 只包含Java 8测试
  include("**/*Test.class", "**/*TestKt.class")
  exclude("**/java17/*Test.class")

  outputs.cacheIf { false }
  outputs.upToDateWhen { false }
  // 使用Java 8
  javaLauncher.set(javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(8))
  })
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}

tasks.register<Test>("testJava17") {
  group = "Verification"
  description = "Runs Java 17 specific tests"

  testClassesDirs = sourceSets["test"].output.classesDirs
  classpath = sourceSets["test"].runtimeClasspath

  // 只包含Java 17测试
  include("**/java17/*Test.class")

  outputs.cacheIf { false }
  outputs.upToDateWhen { false }
  // 使用Java 17
  javaLauncher.set(javaToolchains.launcherFor {
    languageVersion.set(JavaLanguageVersion.of(17))
  })
  useJUnitPlatform()
  finalizedBy(tasks.jacocoTestReport)
}