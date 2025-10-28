plugins {
  `kotlin-dsl`
  //`java-gradle-plugin`
  //`groovy-gradle-plugin`
}

repositories {
  maven {
    val xGradlePluginRepo: String by project
    url = uri(xGradlePluginRepo)
  }
  mavenCentral()
  gradlePluginPortal()
}

dependencies {
  //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
  //implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
  //testImplementation("org.testng:testng:7.5.1")
  implementation(kotlin("stdlib"))
  testImplementation(kotlin("test"))
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