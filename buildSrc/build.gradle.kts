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
    create("pLogger") {
      id = "kitva.build.pLogger"
      implementationClass = "PluginLogger"
    }
  }
}

tasks.test {
  useJUnitPlatform()
}