plugins {
  `java-platform`
  id("fs")
  id("fs-publish")
}

val publishType by extra { "pom" }

description = "Dependencies management of fs."

javaPlatform {
  allowDependencies()
}

dependencies {
  //api(platform("xyz.srclab.dependencies:srclab-dependencies:0.0.1"))

  constraints {

    //lombok
    api("org.projectlombok:lombok:1.18.30")

    //jsr305
    //api("com.google.code.findbugs:jsr305:3.0.2")

    //spring
    api("org.springframework:spring-core:5.3.30")
    api("org.springframework:spring-beans:5.3.30")

    //test
    api("org.junit.jupiter:junit-jupiter-api:5.14.0")
    api("org.junit.jupiter:junit-jupiter-engine:5.14.0")
    api("org.junit.platform:junit-platform-launcher:1.14.0")
    //api("org.testng:testng:7.5.1")
    api("org.openjdk.jmh:jmh-core:1.37")
    api("org.openjdk.jmh:jmh-generator-annprocess:1.37")
    api("org.mockito:mockito-core:5.5.0")

    //commons
    api("org.apache.commons:commons-lang3:3.13.0")
    api("org.apache.commons:commons-collections4:4.4")
    api("commons-beanutils:commons-beanutils:1.9.4")
    api("commons-io:commons-io:2.14.0")
    api("commons-codec:commons-codec:1.16.0")
    api("cn.hutool:hutool-all:5.8.22")
    api("org.jboss:jboss-vfs:3.3.2.Final")

    //cache
    api("com.google.guava:guava:32.1.3-jre")
    api("com.github.ben-manes.caffeine:caffeine:2.9.3")

    //security
    api("org.bouncycastle:bcprov-jdk18on:1.76")
    api("org.bouncycastle:bcpkix-jdk18on:1.79")

    //kotlin
    val kotlinVersion: String by project
    api("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    api("org.jetbrains.kotlin:kotlin-test-testng:$kotlinVersion")

    //generator
    //api("cglib:cglib:3.3.0")
    api("net.bytebuddy:byte-buddy:1.18.1")
    api("net.bytebuddy:byte-buddy-agent:1.18.1")

    //network
    api("io.netty:netty-all:4.1.100.Final")
    api("jakarta.annotation:jakarta.annotation-api:2.1.1")
    api("org.eclipse.jetty:jetty-bom:9.4.58.v20250814")

    //template
    api("org.apache.velocity:velocity-engine-core:2.3")

    //protobuf
    api("com.google.protobuf:protobuf-java:3.24.4")

    //logging
    api("org.slf4j:slf4j-api:2.0.9")

    //config
    api("org.yaml:snakeyaml:2.2")
  }
}