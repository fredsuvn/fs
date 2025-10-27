apply(plugin = "kitva.build.pLogger")

project.repositories {
  mavenLocal()
  maven {
    val myMavenRepo: String by project
    url = uri(myMavenRepo)
  }
  mavenCentral()
  //jcenter()
}



