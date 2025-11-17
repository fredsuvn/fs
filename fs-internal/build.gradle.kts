plugins {
  `java-library`
  jacoco
  id("fs")
}

description = "Internal supporting for fs."

dependencies {
  implementation(platform(project(":fs-dependencies")))
  implementation(project(":fs-annotations"))
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
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
}

tasks.named<Javadoc>("javadoc") {
  enabled = false
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
  reports {
    html.required = false
  }
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = project.property("javaCompatibleLang") as JavaLanguageVersion
  }
}

jacoco {
  val jacocoToolVersion: String by project
  toolVersion = jacocoToolVersion
}

tasks.jacocoTestReport {
  dependsOn(tasks.test)
  reports {
    html.required = false
    xml.required = false
    csv.required = false
  }
}