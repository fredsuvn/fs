plugins {
  id("kitva")
  id("java-library")
  id("kitva-publish")
}

description = "Internal support of KitVa."

dependencies {
  implementation(platform(project(":kitva-dependencies")))
  implementation(project(":kitva-annotations"))
  api("org.junit.jupiter:junit-jupiter-api")
  runtimeOnly("org.junit.jupiter:junit-jupiter-engine")
  runtimeOnly("org.junit.platform:junit-platform-launcher")
  api("org.openjdk.jmh:jmh-core")
  api("io.netty:netty-all")
  api("cn.hutool:hutool-all")
  api("org.apache.velocity:velocity-engine-core")
  api("org.slf4j:slf4j-api")
  api("org.apache.commons:commons-lang3")
  api("org.apache.commons:commons-collections4")
  api("commons-beanutils:commons-beanutils")
  api("commons-io:commons-io")
  api("commons-codec:commons-codec")
  api("com.google.guava:guava")
  api("com.github.ben-manes.caffeine:caffeine")
  api("org.yaml:snakeyaml")
  api("org.bouncycastle:bcprov-jdk18on")
  api("org.springframework:spring-core")
  api("cglib:cglib")
  api("org.springframework:spring-beans")
  api("org.projectlombok:lombok")
  api("org.mockito:mockito-core")
  api("org.jboss:jboss-vfs")
}

java {
  toolchain {
    val toJavaVersion: JavaLanguageVersion by project
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

tasks.clean {
  //delete(tasks.javadoc.get().destinationDir)
}

tasks.register("cleanWithJavadoc") {
  dependsOn(tasks.clean)
  group = "build"
  doLast {
    delete(tasks.javadoc.get().destinationDir)
  }
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
}