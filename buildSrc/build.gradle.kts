plugins {
  `kotlin-dsl`
  //`java-gradle-plugin`
  //`groovy-gradle-plugin`
}

repositories {
  mavenLocal()
  maven {
    val xGradlePluginRepo: String by project
    url = uri(xGradlePluginRepo)
  }
  mavenCentral()
  gradlePluginPortal()
}

val kotlinVersion: String by project

dependencies {
  //implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
  implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
  testImplementation("org.jetbrains.kotlin:kotlin-test:${kotlinVersion}")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:${kotlinVersion}")
  testImplementation(gradleTestKit())
}

gradlePlugin {
  plugins {
    create("fsLogger") {
      id = "fs.build.fsLogger"
      implementationClass = "space.sunqian.fs.build.gradle.FsBuildLogger"
    }
  }
}

tasks.test {
  useJUnitPlatform()
}