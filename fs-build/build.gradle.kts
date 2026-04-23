plugins {
  //`java-library`
  kotlin("jvm")
  id("fs")
}

description = "Build project for fs, provides annotation processor and other build utilities."

dependencies {
  //implementation("org.jetbrains.dokka:dokka-gradle-plugin:$dokkaVersion")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  testImplementation(kotlin("test"))
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
}

val javaVersionFrom = project.property("javaLangVersionFrom") as JavaLanguageVersion
val javaVersionTo = project.property("javaLangVersionTo") as JavaLanguageVersion

sourceSets {
  main {
    java {
      srcDirs("src/main/kotlin")
    }
    kotlin {
      srcDirs("src/main/kotlin")
    }
  }
  test {
    java {
      srcDirs("src/test/kotlin")
    }
    kotlin {
      srcDirs("src/test/kotlin")
    }
  }
}

java {
  toolchain {
    languageVersion = javaVersionFrom
  }
  targetCompatibility = JavaVersion.toVersion(javaVersionFrom)
}

kotlin {
  jvmToolchain(javaVersionFrom.toString().toInt())
}

tasks.test {
  include("**/*Test.class", "**/*TestKt.class")
  useJUnitPlatform()
  reports {
    html.required = false
  }
  javaLauncher = javaToolchains.launcherFor {
    languageVersion = javaVersionFrom
  }
}