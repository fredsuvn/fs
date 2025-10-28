plugins {
  id("kitva")
  id("java-library")
  id("kitva-publish")
  id("com.google.protobuf") version (Versions.protobuf)
  //jacoco
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
  toolchain {
    languageVersion.set(toJavaVersion)
  }
  withJavadocJar()
  withSourcesJar()
}

tasks.withType<Javadoc>().configureEach {
  destinationDir = file("$projectDir/docs/javadoc")
  (options as StandardJavadocDocletOptions).apply {
    encoding = "UTF-8"
    locale = "en_US"
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

sourceSets {
  main {
    java {
      if (toJavaVersion.canCompileOrRun(JavaLanguageVersion.of(17))) {
        srcDirs("src/main/java17")
      } else if (toJavaVersion.canCompileOrRun(JavaLanguageVersion.of(8))) {
        srcDirs("src/main/java8")
      }
    }
  }
  test {
    proto {
      //srcDirs("src/test/proto")
    }
    java {
      if (toJavaVersion.canCompileOrRun(JavaLanguageVersion.of(17))) {
        srcDirs("src/test/java17")
      } else if (toJavaVersion.canCompileOrRun(JavaLanguageVersion.of(8))) {
        srcDirs("src/test/java8")
      }
      //srcDirs("src/test/proto")
    }
  }
}

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

tasks.named("compileTestJava") {
  dependsOn(tasks.named("generateTestProto"))
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

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
  //finalizedBy(tasks.jacocoTestReport)
}

//jacoco {
//  toolVersion = "0.8.14"
//}

//tasks.jacocoTestReport {
//  reports {
//    html.required.set(true) // 生成HTML报告便于查看[citation:6]
//    //xml.required.set(true)
//  }
//  // 确保报告包含了所有源码，包括 .kts 文件
//  classDirectories.setFrom(sourceSets.main.get().output.asFileTree.matching {
//    // 根据需要调整包含模式
//    include("**/*.class")
//  })
//}

fun deleteProtoGeneratedFiles() {
  delete(protobuf.generatedFilesBaseDir)
  //delete("$generatedPath/temp")
}