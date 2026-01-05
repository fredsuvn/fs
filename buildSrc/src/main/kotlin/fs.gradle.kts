import space.sunqian.fs.build.gradle.plugin.FsBuildLogger

apply(plugin = "fs.build.fsLogger")

project.repositories {
  mavenLocal()
  maven {
    val xMavenRepo: String by project
    url = uri(xMavenRepo)
  }
  mavenCentral()
  //jcenter()
}

val fsLogger: FsBuildLogger by project
fsLogger.info("hello, fs!")

val javaVersionFrom: String by project
val javaVersionTo: String by project
val javaLangVersionFrom: JavaLanguageVersion by extra {
  JavaLanguageVersion.of(javaVersionFrom)
}
val javaLangVersionTo: JavaLanguageVersion by extra {
  JavaLanguageVersion.of(javaVersionTo)
}