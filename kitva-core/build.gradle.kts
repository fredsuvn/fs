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
}

val j17Suffix = "ImplByJ17"

java {
  withJavadocJar()
  withSourcesJar()
  toolchain {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
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
  exclude("**/*$j17Suffix.java")
  javaCompiler = javaToolchains.compilerFor {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
}

val compileJava17 by tasks.registering(JavaCompile::class) {
  dependsOn(tasks.compileJava)
  source = sourceSets.main.get().allJava
  destinationDirectory = file(layout.buildDirectory.dir("/classes/java/main"))
  classpath = tasks.compileJava.get().classpath + files(tasks.compileJava.get().destinationDirectory)
  include("**/*$j17Suffix.java")
  javaCompiler = javaToolchains.compilerFor {
    languageVersion = project.property("javaCurrentLang") as JavaLanguageVersion
  }
}

tasks.named("classes") {
  dependsOn(compileJava17)
}

tasks.compileTestJava {
  source = sourceSets.test.get().allJava
  exclude("**/*${j17Suffix}Test.java")
  javaCompiler = javaToolchains.compilerFor {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
}

val compileTestJava17 by tasks.registering(JavaCompile::class) {
  dependsOn(compileJava17)
  source = sourceSets.test.get().allJava
  include("**/*${j17Suffix}Test.java")
  destinationDirectory = file(layout.buildDirectory.dir("/classes/java/test"))
  classpath = sourceSets.test.get().compileClasspath + files(compileJava17.get().destinationDirectory)
  javaCompiler = javaToolchains.compilerFor {
    languageVersion = project.property("javaCurrentLang") as JavaLanguageVersion
  }
}

tasks.named("compileTestJava") {
  dependsOn(tasks.named("generateTestProto"), compileTestJava17)
}

tasks.named<Jar>("sourcesJar") {
  from(sourceSets.main.get().allJava)
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  exclude("**/*${j17Suffix}Test.class")
  useJUnitPlatform()
  failOnNoDiscoveredTests = false
//  reports {
//    html.required = false
//  }
  //outputs.cacheIf { false }
  //outputs.upToDateWhen { false }
  //finalizedBy(testJava17)
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
}

val testJava17 by tasks.registering(Test::class) {
  dependsOn(compileTestJava17)
  group = "verification"
  testClassesDirs = fileTree(layout.buildDirectory.dir("/classes/java/test"))
  classpath = sourceSets.test.get().runtimeClasspath
  include("**/*${j17Suffix}Test.class")
  include("**/*MultiJvmTest.class")
  useJUnitPlatform()
  failOnNoDiscoveredTests = false
//  reports {
//    html.required = false
//  }
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = project.property("javaCurrentLang") as JavaLanguageVersion
  }
}
tasks.check.get().dependsOn(testJava17)

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